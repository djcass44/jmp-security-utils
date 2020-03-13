/*
 *   This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.dcas.jmp.spring.security.oauth2.impl

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.scribejava.apis.GoogleApi20
import com.github.scribejava.core.model.OAuthRequest
import com.github.scribejava.core.model.Verb
import dev.castive.log2.logd
import dev.castive.log2.loge
import dev.dcas.jmp.spring.security.model.OAuth2User
import dev.dcas.jmp.spring.security.model.UserProjection
import dev.dcas.jmp.spring.security.oauth2.ProviderConfig
import dev.dcas.util.extend.parse

/**
 * https://stackoverflow.com/a/24646356 - for testing later
 */
class GoogleProvider(
    private val config: ProviderConfig,
    private val objectMapper: ObjectMapper
): AbstractOAuth2Provider(config, GoogleApi20.instance()) {

	@JsonIgnoreProperties(ignoreUnknown = true)
    data class GoogleUser(
        val sub: String,
        val name: String,
        val picture: String,
        val iss: String,
        val aud: String,
        val exp: Long
    ): OAuth2User {
        /**
         * Creates a common interface for oauth2 user information
         */
        override fun project(): UserProjection {
            return UserProjection(sub, name, picture, "google")
        }
    }

    override fun isTokenValid(accessToken: String): Boolean {
        val request = OAuthRequest(Verb.GET, "${config.apiUrl}/v1/tokeninfo")
        service.signRequest(accessToken, request)
        val response = service.execute(request)
        // if the request failed, return false
        if(!response.isSuccessful) {
            "Got unsuccessful response from Google's /v1/tokeninfo".logd(javaClass)
            return false
        }
        // verify that our clientId matches the one in the token
        return objectMapper.readTree(response.body).path("audience").asText() == config.clientId
    }

    override fun getUserInformation(accessToken: String): UserProjection? {
        // create and sign the request
        val request = OAuthRequest(Verb.GET, "${config.apiUrl}/v3/userinfo")
        service.signRequest(accessToken, request)
        val response = service.execute(request)
        // if the request failed, return null
        if(!response.isSuccessful) {
            "Failed to load user information: ${response.body}".loge(javaClass)
            return null
        }
        // extract the relevant information
        return response.body.parse(GoogleUser::class.java).project()
    }
}