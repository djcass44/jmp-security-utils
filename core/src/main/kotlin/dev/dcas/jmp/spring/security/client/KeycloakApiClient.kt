/*
 *   This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.dcas.jmp.spring.security.client

import dev.dcas.jmp.spring.security.SecurityConstants
import dev.dcas.jmp.spring.security.oauth2.impl.KeycloakProvider
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import java.net.URI

@FeignClient(value = "oauth2-keycloak", url = "https://")
interface KeycloakApiClient {

	/**
	 * Get the users profile information
	 */
	@GetMapping("/auth/realms/{realm}/protocol/openid-connect/userinfo")
	fun getUser(
		baseUrl: URI,
		@RequestHeader(SecurityConstants.authHeader) accessToken: String,
		@PathVariable realm: String
	): KeycloakProvider.KeycloakUser
}