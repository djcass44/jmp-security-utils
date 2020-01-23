/*
 *   This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.dcas.jmp.spring.security.oauth2

import com.fasterxml.jackson.databind.ObjectMapper
import dev.castive.log2.*
import dev.dcas.jmp.spring.security.SecurityConstants
import dev.dcas.jmp.spring.security.model.TokenProvider
import dev.dcas.jmp.spring.security.model.UserPrincipal
import dev.dcas.jmp.spring.security.model.entity.UserEntity
import dev.dcas.jmp.spring.security.model.repo.GroupRepository
import dev.dcas.jmp.spring.security.model.repo.SessionRepository
import dev.dcas.jmp.spring.security.model.repo.UserRepository
import dev.dcas.jmp.spring.security.oauth2.impl.AbstractOAuth2Provider
import dev.dcas.jmp.spring.security.oauth2.impl.GitHubProvider
import dev.dcas.jmp.spring.security.oauth2.impl.GoogleProvider
import dev.dcas.jmp.spring.security.props.SecurityProps
import dev.dcas.jmp.spring.security.util.NotFoundResponse
import dev.dcas.jmp.spring.security.util.Responses
import dev.dcas.util.cache.TimedCache
import dev.dcas.util.extend.ellipsize
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct
import javax.servlet.http.HttpServletRequest

@Service
class OAuth2TokenProvider @Autowired constructor(
    private val oauth2Config: SecurityProps,
    private val userRepo: UserRepository,
    private val groupRepo: GroupRepository,
    private val sessionRepo: SessionRepository,
    private val passwordEncoder: PasswordEncoder,
    private val objectMapper: ObjectMapper
): TokenProvider {
    // hold tokens for 60 seconds (tick every 10)
    private val tokenCache = TimedCache<String, String>(6, tickDelay = 10_000L)
    private var counter = 0

    private val providers = mutableSetOf<AbstractOAuth2Provider>()


    @PostConstruct
    fun init() {
        "Found ${oauth2Config.oauth2.size} oauth2 configurations listed".logv(javaClass)
        // build our providers
        oauth2Config.oauth2.filter { it.enabled }.forEach {
            when(it.name) {
                "github" -> providers.add(GitHubProvider(it))
                "google" -> providers.add(GoogleProvider(it, objectMapper))
                else -> "Found unsupported OAuth2 provider: ${it.name}".loga(javaClass)
            }
        }
        "Activated ${providers.size} oauth2 provider(s): [${providers.joinToString(", ") { it.name }}]".logi(javaClass)
        providers.forEach {
            groupRepo.findFirstByName("_oauth2/${it.name}") ?: groupRepo.create(
                // create the group if it doesn't exist
                name = "_oauth2/${it.name}",
                source = it.name,
                defaultFor = "oauth2/${it.name}"
            )
        }
        "Finished oauth2 group checks".logv(javaClass)
    }

    /**
     * Checks whether an OAuth2 token is valid or was valid recently
     * Reduces the amount of load put against the oauth2 provider by caching tokens for 60 seconds
     * @return true if token is was found in the cache or is actually valid
     */
    override fun isTokenValid(token: String, source: String): Boolean {
        val provider = findProviderByName(source.removePrefix("${SecurityConstants.sourceOAuth2}/")) ?: run {
            "Unable to find wired provider with name: $source".logi(javaClass)
            return false
        }
        counter++
        if(counter > 25) {
            "Token cache contains ${tokenCache.size()} elements".logd(javaClass)
            counter = 0
        }

        val cached = tokenCache[token]
        if(cached != null)
            return true
        if(provider.isTokenValid(token)) {
            tokenCache[token] = token
            return true
        }
        return false
    }

    fun createUser(accessToken: String, refreshToken: String, provider: AbstractOAuth2Provider): Boolean {
        val userData = provider.getUserInformation(accessToken)
        if(userData == null) {
            "Failed to get user information for user with token: ${accessToken.ellipsize(24)}".loge(javaClass)
            return false
        }
        // users are prepended with oauth2 to ensure there are no collisions
        val oauthUsername = "oauth2/${userData.username}"
        // only create the user if they don't exist
        if(!userRepo.existsByUsername(oauthUsername)) {
            // create the user
            val user = userRepo.createWithData("${SecurityConstants.sourceOAuth2}/${provider.name}", oauthUsername, userData)
            // create a session for the new user
            newSession(accessToken, refreshToken, user)
        }
        else {
            "User already exists: $oauthUsername".logi(javaClass)
            // create/update the session for the existing user
            val user = userRepo.findFirstByUsername(oauthUsername)?.let {
                userRepo.update(userData)
            }
            newSession(accessToken, refreshToken, user)
        }
        return true
    }

    /**
     * Create and update the sessions for the user
     * @param user: what user we want to create the session for. This can be null if there is an active session (e.g. for refreshing)
     */
    private fun newSession(requestToken: String, refreshToken: String, user: UserEntity?, oldToken: String = refreshToken) {
        val existingSession = sessionRepo.findFirstByRefreshTokenAndActiveTrue(oldToken)?.let {
            sessionRepo.disable(it)
        }

        if(user == null && existingSession == null) {
            "Unable to create session as we have no context of the user".logw(javaClass)
            return
        }
        // create the new session
        sessionRepo.create(
            passwordEncoder.encode(requestToken),
            passwordEncoder.encode(refreshToken),
            user ?: existingSession!!.user
        )
    }

    override fun getAuthentication(token: String): Authentication? {
        val session = sessionRepo.findFirstByRequestTokenAndActiveTrue(token) ?: return null
        val user = UserPrincipal(session.user)
        return UsernamePasswordAuthenticationToken(user, "", user.authorities)
    }

    /**
     * Extract an OAuth2 token from a request header
     */
    override fun resolveToken(request: HttpServletRequest): String? {
        val source = request.getHeader(SecurityConstants.sourceHeader)
        // oauth2 must advertise source as oauth2/name (e.g. oauth2/github, oauth/google)
        if(!source.startsWith("${SecurityConstants.sourceOAuth2}/"))
            return null
        val token = request.getHeader(SecurityConstants.authHeader) ?: return null
        // token MUST start with 'Bearer '
        if(token.startsWith("Bearer "))
            return token.substring(7)
        return null
    }

    /**
     * Find an OAuth2 provider by its common name (e.g. 'github', 'google')
     * @param name: the name of the provider
     * @return AbstractOAuth2Provider or throw http 404 if it can't be found
     */
    fun findProviderByNameOrThrow(name: String): AbstractOAuth2Provider = findProviderByName(name) ?: throw NotFoundResponse(
        Responses.NOT_FOUND_PROVIDER)

    /**
     * Find an OAuth2 provider by its common name (e.g. 'github', 'google')
     * @param name: the name of the provider
     * @return AbstractOAuth2Provider or null if it can't be found
     */
    fun findProviderByName(name: String): AbstractOAuth2Provider? = providers.firstOrNull {
        it.name == name
    }
}