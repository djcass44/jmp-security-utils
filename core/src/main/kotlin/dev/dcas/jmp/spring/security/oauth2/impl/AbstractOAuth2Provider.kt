/*
 *   This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.dcas.jmp.spring.security.oauth2.impl

import com.github.scribejava.core.builder.ServiceBuilder
import com.github.scribejava.core.builder.api.DefaultApi20
import com.github.scribejava.core.model.OAuth2AccessToken
import com.github.scribejava.core.oauth.OAuth20Service
import dev.castive.log2.logv
import dev.dcas.jmp.spring.security.model.UserProjection
import dev.dcas.jmp.spring.security.oauth2.ProviderConfig
import dev.dcas.util.extend.base64Url
import dev.dcas.util.extend.decodeBase64Url
import dev.dcas.util.extend.randomString
import java.util.concurrent.Future
import javax.annotation.PostConstruct

abstract class AbstractOAuth2Provider(
    private val provider: ProviderConfig,
    api: DefaultApi20
) {
    companion object {
        fun parseState(state: String): Triple<String, String, String> {
            val (name ,meta, code) = state.decodeBase64Url().split(":", limit = 3)
            return Triple(name, meta, code)
        }
    }

    val name: String = provider.name

    protected val service: OAuth20Service = ServiceBuilder(provider.clientId)
        .apiSecret(provider.clientSecret)
        .callback(provider.callbackUrl)
        .defaultScope(provider.scope)
        .build(api)

    @PostConstruct
    fun init() {
        "Initialising bean for OAuth2 provider: ${provider.name}".logv(javaClass)
    }

    /**
     * Get the url for the consent screen to redirect the user
     */
    fun getAuthoriseUrl(): String = service.getAuthorizationUrl(getState())

    /**
     * Get an access token using the consent code
     */
    fun getAccessToken(code: String): OAuth2AccessToken = service.getAccessToken(code)

    /**
     * Get a new access token using our refresh token
     */
    fun refreshToken(refreshToken: String): OAuth2AccessToken = service.refreshAccessToken(refreshToken)

    /**
     * Used for logout
     * Revokes the oauth2 token (assuming the api supports revoking tokens)
     * this::revokeTokenAsync is preferred
     */
    open fun revokeToken(accessToken: String) = service.revokeToken(accessToken)

    /**
     * Used for logout
     * Async is preferred because the user isn't waiting on the result
     */
    open fun revokeTokenAsync(accessToken: String): Future<*> = service.revokeTokenAsync(accessToken)

    /**
     * Check if the access token is still valid
     */
    abstract fun isTokenValid(accessToken: String): Boolean

    abstract fun getUserInformation(accessToken: String): UserProjection?

    private fun getState(meta: String = ""): String = "${provider.name}:$meta:${32.randomString()}".base64Url()
}