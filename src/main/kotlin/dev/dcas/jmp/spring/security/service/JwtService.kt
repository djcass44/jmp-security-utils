/*
 *   This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.dcas.jmp.spring.security.service

import dev.castive.log2.logi
import dev.dcas.jmp.spring.security.jwt.JwtTokenProvider
import dev.dcas.jmp.spring.security.model.AuthToken
import dev.dcas.jmp.spring.security.model.entity.SessionEntity
import dev.dcas.jmp.spring.security.model.entity.UserEntity
import dev.dcas.jmp.spring.security.model.repo.SessionRepository
import dev.dcas.jmp.spring.security.model.repo.UserRepository
import dev.dcas.jmp.spring.security.util.NotFoundResponse
import dev.dcas.jmp.spring.security.util.Responses
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class JwtService @Autowired constructor(
    private val userRepo: UserRepository,
    private val sessionRepo: SessionRepository,
    private val jwtTokenProvider: JwtTokenProvider,
    private val passwordEncoder: PasswordEncoder
) {
    /**
     * Creates a new token and session for a specified user
     */
    fun createToken(user: UserEntity): AuthToken {
        "Generating new request token for ${user.username}".logi(javaClass)
        val (session, request, refresh) = createSession(user, jwtTokenProvider, passwordEncoder)
        sessionRepo.update(session)
        return AuthToken(request, refresh, user.source)
    }

    /**
     * Uses a refresh token to create a new access token for a specified user
     * Disables old session
     */
    fun refreshToken(refreshToken: String): AuthToken {
        val username = jwtTokenProvider.getUsername(refreshToken) ?: throw NotFoundResponse(Responses.NOT_FOUND_USER)
        val user = userRepo.findFirstByUsername(username) ?: throw NotFoundResponse(Responses.NOT_FOUND_USER)
        // must have an existing session in order to refresh
        val existingSession = sessionRepo.findFirstByUserAndRefreshTokenAndActiveTrue(user, refreshToken) ?: throw NotFoundResponse(Responses.NOT_FOUND_SESSION)
        // invalidate the old session
        sessionRepo.disable(existingSession)
        // create a new session
        val (session, request, refresh) = createSession(user, jwtTokenProvider, passwordEncoder)
        sessionRepo.update(session)
        "Generating new refresh token for ${user.username}".logi(javaClass)
        return AuthToken(request, refresh, user.source)
    }

    /**
     * Revokes a specified token
     */
    fun revokeToken(token: String) {
        val username = jwtTokenProvider.getUsername(token) ?: throw NotFoundResponse(Responses.NOT_FOUND_USER)
        val user = userRepo.findFirstByUsername(username) ?: throw NotFoundResponse(Responses.NOT_FOUND_USER)
        val session = sessionRepo.findFirstByUserAndRequestTokenAndActiveTrue(user, token) ?: throw NotFoundResponse(Responses.NOT_FOUND_SESSION)
        // disable the session
        sessionRepo.disable(session)
        "Disabled session ${session.id} owned by $username".logi(javaClass)
    }

    /**
     * Create a session for a user and generate tokens
     */
    private fun createSession(user: UserEntity, provider: JwtTokenProvider, encoder: PasswordEncoder): Triple<SessionEntity, String, String> {
        val request = provider.createRequestToken(user.username, user.roles)
        val refresh = provider.createRefreshToken(user.username, user.roles)
        val session = sessionRepo.create(encoder.encode(request), encoder.encode(refresh), user)
        return Triple(session, request, refresh)
    }
}