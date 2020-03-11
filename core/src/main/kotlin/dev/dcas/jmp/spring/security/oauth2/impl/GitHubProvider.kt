/*
 *   This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.dcas.jmp.spring.security.oauth2.impl

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.scribejava.apis.GitHubApi
import dev.castive.log2.loge
import dev.dcas.jmp.spring.security.client.GitHubApiClient
import dev.dcas.jmp.spring.security.model.OAuth2User
import dev.dcas.jmp.spring.security.model.UserProjection
import dev.dcas.jmp.spring.security.oauth2.ProviderConfig
import dev.dcas.util.extend.toBasic
import org.springframework.http.HttpStatus

class GitHubProvider(
	private val config: ProviderConfig,
	private val client: GitHubApiClient
): AbstractOAuth2Provider(config, GitHubApi.instance()) {

	@JsonIgnoreProperties(ignoreUnknown = true)
    data class GitHubUser(
        val login: String,
        val id: Int,
        @JsonProperty("avatar_url")
        val avatarUrl: String,
        val type: String,
        val name: String
    ): OAuth2User {
        /**
         * Creates a common interface for oauth2 user information
         */
        override fun project(): UserProjection {
            return UserProjection(login, name, avatarUrl, "github")
        }
    }

    override fun isTokenValid(accessToken: String): Boolean {
		val response = client.isTokenValid(GitHubApiClient.GitHub3Authentication("Bearer $accessToken"), (config.clientId to config.clientSecret).toBasic(), config.clientId)
        return response.status() == HttpStatus.OK.value()
    }

    override fun getUserInformation(accessToken: String): UserProjection? {
		return kotlin.runCatching {
			client.getUser(accessToken)
		}.onFailure {
			"Failed to lookup user".loge(javaClass, it)
		}.getOrNull()?.project()
    }
}