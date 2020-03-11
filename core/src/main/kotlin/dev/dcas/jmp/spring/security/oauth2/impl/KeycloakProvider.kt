/*
 *   This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.dcas.jmp.spring.security.oauth2.impl

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.scribejava.apis.KeycloakApi
import dev.castive.log2.loge
import dev.dcas.jmp.spring.security.client.KeycloakApiClient
import dev.dcas.jmp.spring.security.model.OAuth2User
import dev.dcas.jmp.spring.security.model.UserProjection
import dev.dcas.jmp.spring.security.oauth2.ProviderConfig
import java.net.URI

class KeycloakProvider(
	private val config: ProviderConfig,
	private val client: KeycloakApiClient
	// reuse scope as realm
): AbstractOAuth2Provider(config, KeycloakApi.instance(config.apiUrl, config.scope)) {

	@JsonIgnoreProperties(ignoreUnknown = true)
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
		return kotlin.runCatching {
			client.getUser(URI.create(config.apiUrl), accessToken, config.scope)
		}.onFailure {
			"Failed to lookup user".loge(javaClass, it)
		}.getOrNull()?.project()
    }
}