/*
 *   This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.dcas.jmp.spring.security.oauth2.impl

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.scribejava.apis.GitHubApi
import com.github.scribejava.core.model.OAuthRequest
import com.github.scribejava.core.model.Verb
import dev.castive.log2.loge
import dev.dcas.jmp.spring.security.SecurityConstants
import dev.dcas.jmp.spring.security.model.OAuth2User
import dev.dcas.jmp.spring.security.model.UserProjection
import dev.dcas.jmp.spring.security.oauth2.ProviderConfig
import dev.dcas.util.extend.parse
import dev.dcas.util.extend.toBasic

class GitHubProvider(private val config: ProviderConfig): AbstractOAuth2Provider(config, GitHubApi.instance()) {

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
        val request = OAuthRequest(Verb.GET, "${config.apiUrl}/applications/${config.clientId}/tokens/$accessToken").apply {
            addHeader(SecurityConstants.authHeader, (config.clientId to config.clientSecret).toBasic())
        }
        val response = service.execute(request)
        return response.isSuccessful
    }

    override fun getUserInformation(accessToken: String): UserProjection? {
        val request = OAuthRequest(Verb.GET, "${config.apiUrl}/user").apply {
            addHeader(SecurityConstants.authHeader, "Bearer $accessToken")
        }
        val response = service.execute(request)
        if(!response.isSuccessful) {
            "Failed to load user information: ${response.body}".loge(javaClass)
            return null
        }
        return kotlin.runCatching {
            response.body.parse(GitHubUser::class.java).project()
        }.onFailure {
            "Failed to parse response body".loge(javaClass, it)
        }.getOrNull()
    }
}