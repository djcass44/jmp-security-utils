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

import com.github.scribejava.core.builder.api.DefaultApi20
import com.github.scribejava.core.extractors.OAuth2AccessTokenJsonExtractor
import com.github.scribejava.core.extractors.TokenExtractor
import com.github.scribejava.core.model.OAuth2AccessToken
import com.github.scribejava.core.model.Verb

class GitLabApi(private val apiUrl: String): DefaultApi20() {

	override fun getAccessTokenVerb(): Verb {
		return Verb.POST
	}

	override fun getAccessTokenEndpoint(): String {
		return "${apiUrl}/oauth/token"
	}

	override fun getAuthorizationBaseUrl(): String {
		return "${apiUrl}/oauth/authorize"
	}

	override fun getAccessTokenExtractor(): TokenExtractor<OAuth2AccessToken> = OAuth2AccessTokenJsonExtractor.instance()
}