/*
 *   This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.dcas.jmp.spring.security.client

import dev.dcas.jmp.spring.security.SecurityConstants
import dev.dcas.jmp.spring.security.oauth2.impl.GitLabProvider
import feign.Response
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import java.net.URI

@FeignClient(value = "oauth2-gitlab", url = "https://")
interface GitLabApiClient {
	/**
	 * Check whether the users accessToken is valid
	 * Return the full response because we only care about the status code
	 */
	@PostMapping("/api/v4/oauth/token/info")
	fun isTokenValid(
		baseUrl: URI,
		@RequestHeader(SecurityConstants.authHeader) accessToken: String
	): Response

	/**
	 * Get the users profile information
	 */
	@GetMapping("/api/v4/user")
	fun getUser(
		baseUrl: URI,
		@RequestHeader(SecurityConstants.authHeader) accessToken: String
	): GitLabProvider.GitLabUser
}