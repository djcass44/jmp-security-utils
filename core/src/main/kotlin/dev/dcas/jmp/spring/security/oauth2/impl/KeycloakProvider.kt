/*
 *   This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.dcas.jmp.spring.security.oauth2.impl

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.scribejava.apis.KeycloakApi
import com.github.scribejava.core.model.OAuthRequest
import com.github.scribejava.core.model.Verb
import dev.castive.log2.loge
import dev.dcas.jmp.spring.security.SecurityConstants
import dev.dcas.jmp.spring.security.model.OAuth2User
import dev.dcas.jmp.spring.security.model.UserProjection
import dev.dcas.jmp.spring.security.oauth2.ProviderConfig

class KeycloakProvider(
	private val config: ProviderConfig,
	private val objectMapper: ObjectMapper
	// reuse scope as realm
): AbstractOAuth2Provider(config, KeycloakApi.instance(config.apiUrl, config.scope)) {

    data class KeycloakUser(
		val sub: String,
		@JsonProperty("email_verified")
		val emailVerified: Boolean,
		val name: String,
		@JsonProperty("preferred_username")
		val preferredUsername: String,
		@JsonProperty("given_name")
		val givenName: String,
		@JsonProperty("family_name")
		val familyName: String,
		val email: String
    ): OAuth2User {
        /**
         * Creates a common interface for oauth2 user information
         */
        override fun project(): UserProjection {
            return UserProjection(preferredUsername, name, null, "keycloak")
        }
    }

	/**
	 * Assume the token is always valid, if it doesn't work then it's probably invalid
	 */
    override fun isTokenValid(accessToken: String): Boolean = true

    override fun getUserInformation(accessToken: String): UserProjection? {
        val request = OAuthRequest(Verb.GET, "${config.apiUrl}/auth/realms/${config.scope}/protocol/openid-connect/userinfo").apply {
            addHeader(SecurityConstants.authHeader, "Bearer $accessToken")
        }
        val response = service.execute(request)
        if(!response.isSuccessful) {
            "Failed to load user information: ${response.body}".loge(javaClass)
            return null
        }
        return kotlin.runCatching {
	        objectMapper.readValue<KeycloakUser>(response.body).project()
        }.onFailure {
            "Failed to parse response body".loge(javaClass, it)
        }.getOrNull()
    }
}