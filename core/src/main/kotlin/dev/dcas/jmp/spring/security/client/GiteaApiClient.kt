/*
 *   This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.dcas.jmp.spring.security.client

import com.fasterxml.jackson.annotation.JsonProperty
import dev.dcas.jmp.spring.security.SecurityConstants
import dev.dcas.jmp.spring.security.oauth2.impl.GiteaProvider
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import java.net.URI

@FeignClient(value = "oauth2-gitea", url = "https://")
interface GiteaApiClient {

	data class GiteaToken(
		@JsonProperty("access_token")
		val accessToken: String,
		@JsonProperty("token_type")
		val tokenType: String,
		@JsonProperty("expires_in")
		val expiresIn: Long,
		@JsonProperty("refresh_token")
		val refreshToken: String
	)

	/**
	 * Get the users profile information
	 */
	@GetMapping("/api/v1/user")
	fun getUser(
		baseUrl: URI,
		@RequestHeader(SecurityConstants.authHeader) accessToken: String
	): GiteaProvider.GiteaUser
}