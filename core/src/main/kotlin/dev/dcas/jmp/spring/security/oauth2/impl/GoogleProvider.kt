/*
 *   This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.dcas.jmp.spring.security.oauth2.impl

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.scribejava.apis.GoogleApi20
import dev.castive.log2.logd
import dev.castive.log2.loge
import dev.dcas.jmp.spring.security.client.GoogleApiClient
import dev.dcas.jmp.spring.security.model.OAuth2User
import dev.dcas.jmp.spring.security.model.UserProjection
import dev.dcas.jmp.spring.security.oauth2.ProviderConfig
import org.springframework.http.HttpStatus

class GoogleProvider(
	private val config: ProviderConfig,
	private val client: GoogleApiClient,
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
		val response = client.isTokenValid("Bearer $accessToken")
        // if the request failed, return false
        if(response.statusCode != HttpStatus.OK) {
            "Got unsuccessful response from Google's /v1/tokeninfo".logd(javaClass)
            return false
        }
		if(!response.hasBody()) {
			"No body when attempting to verify oauth2 access token".loge(javaClass)
			return false
		}
        // verify that our clientId matches the one in the token
        return objectMapper.readTree(response.body).path("audience").asText() == config.clientId
    }

    override fun getUserInformation(accessToken: String): UserProjection? {
		return kotlin.runCatching {
			client.getUser("Bearer $accessToken")
		}.onFailure {
			"Failed to lookup user".loge(javaClass, it)
		}.getOrNull()?.project()
    }
}