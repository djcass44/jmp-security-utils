/*
 *   This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.dcas.jmp.spring.security.client

import dev.dcas.jmp.spring.security.SecurityConstants
import dev.dcas.jmp.spring.security.oauth2.impl.GoogleProvider
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader

@FeignClient(value = "oauth2-google", url = "https://www.googleapis.com/oauth2")
interface GoogleApiClient {

	/**
	 * Check whether the users accessToken is valid
	 * Return the full response because we only care about the status code
	 */
	@GetMapping("/v1/tokeninfo")
	fun isTokenValid(
		@RequestHeader(SecurityConstants.authHeader) accessToken: String
	): ResponseEntity<String>

	/**
	 * Get the users profile information
	 */
	@GetMapping("/v3/userinfo")
	fun getUser(
		@RequestHeader(SecurityConstants.authHeader) accessToken: String
	): GoogleProvider.GoogleUser
}