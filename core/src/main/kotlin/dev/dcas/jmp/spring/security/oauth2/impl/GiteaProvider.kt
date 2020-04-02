/*
 *   This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.dcas.jmp.spring.security.oauth2.impl

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import dev.castive.log2.loge
import dev.dcas.jmp.spring.security.client.GiteaApiClient
import dev.dcas.jmp.spring.security.model.OAuth2User
import dev.dcas.jmp.spring.security.model.UserProjection
import dev.dcas.jmp.spring.security.oauth2.GiteaApi
import dev.dcas.jmp.spring.security.oauth2.ProviderConfig
import java.net.URI

class GiteaProvider(
	config: ProviderConfig,
	private val client: GiteaApiClient,
	objectMapper: ObjectMapper
): AbstractOAuth2Provider(config, GiteaApi(config.apiUrl, objectMapper)) {

	@JsonIgnoreProperties(ignoreUnknown = true)
    data class GiteaUser(
        val login: String,
        val id: Int,
        @JsonProperty("avatar_url")
        val avatarUrl: String,
		@JsonProperty("is_admin")
        val isAdmin: String,
		@JsonProperty("full_name")
        val fullName: String
    ): OAuth2User {
        /**
         * Creates a common interface for oauth2 user information
         */
        override fun project(): UserProjection {
            return UserProjection(login, fullName, avatarUrl, "github")
        }
    }

	private val apiUri = URI.create(config.apiUrl)

	/**
	 * If we can get our user-info, then we have a valid token
	 */
    override fun isTokenValid(accessToken: String): Boolean {
		return getUserInformation(accessToken) != null
	}

    override fun getUserInformation(accessToken: String): UserProjection? {
		return kotlin.runCatching {
			client.getUser(apiUri, "Bearer $accessToken")
		}.onFailure {
			"Failed to lookup user".loge(javaClass, it)
		}.getOrNull()?.project()
    }
}