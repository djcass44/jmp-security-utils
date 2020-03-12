/*
 *   This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.dcas.jmp.spring.security.client

import com.fasterxml.jackson.annotation.JsonProperty
import dev.dcas.jmp.spring.security.SecurityConstants
import dev.dcas.jmp.spring.security.oauth2.impl.GitHubProvider
import feign.Response
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.*

@FeignClient(value = "oauth2-github", url = "https://api.github.com")
interface GitHubApiClient {
	data class GitHub3Authentication(
		@JsonProperty("access_token")
		val accessToken: String
	)

	/**
	 * Check whether the users accessToken is valid
	 * Return the full response because we only care about the status code
	 */
	@PostMapping("/applications/{clientId}/token")
	fun isTokenValid(
		@RequestBody body: GitHub3Authentication,
		@RequestHeader(SecurityConstants.authHeader) accessToken: String,
		@PathVariable clientId: String
	): Response

	/**
	 * Get the users profile information
	 */
	@GetMapping("/user")
	fun getUser(
		@RequestHeader(SecurityConstants.authHeader) accessToken: String
	): GitHubProvider.GitHubUser
}