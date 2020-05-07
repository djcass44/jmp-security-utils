/*
 *   This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

/*
 *   This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.dcas.jmp.spring.security.oauth2.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.scribejava.core.builder.api.DefaultApi20
import com.github.scribejava.core.extractors.TokenExtractor
import com.github.scribejava.core.model.OAuth2AccessToken
import com.github.scribejava.core.model.Response
import com.github.scribejava.core.model.Verb
import dev.dcas.jmp.spring.security.client.GiteaApiClient

class GiteaApi(private val apiUrl: String, private val objectMapper: ObjectMapper): DefaultApi20() {
	/**
	 * Custom logic to extract token information from authorisation flow
	 * Gitea returns accessToken in the body, compared to other which use uri parameters
	 */
	class GiteaTokenExtractor(private val objectMapper: ObjectMapper): TokenExtractor<OAuth2AccessToken> {
		override fun extract(response: Response): OAuth2AccessToken {
			val body = response.body
			val token = objectMapper.readValue(body, GiteaApiClient.GiteaToken::class.java)
			return OAuth2AccessToken(token.accessToken, token.tokenType, token.expiresIn.toInt(), token.refreshToken, "", body)
		}

	}

	override fun getAccessTokenVerb(): Verb {
		return Verb.POST
	}

	override fun getAccessTokenEndpoint(): String {
		return "${apiUrl}/login/oauth/access_token"
	}

	override fun getAuthorizationBaseUrl(): String {
		return "${apiUrl}/login/oauth/authorize"
	}

	override fun getAccessTokenExtractor(): TokenExtractor<OAuth2AccessToken> =
		GiteaTokenExtractor(objectMapper)
}