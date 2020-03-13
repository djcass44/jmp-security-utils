/*
 *   This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.dcas.jmp.spring.security.jwt

import dev.dcas.jmp.spring.security.SecurityConstants
import dev.dcas.jmp.spring.security.props.JwtProps
import dev.dcas.jmp.spring.security.props.SecurityProps
import dev.dcas.jmp.spring.security.service.UserDetailsService
import dev.dcas.util.crypto.Crypto
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import javax.servlet.http.HttpServletRequest
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class JwtTokenProviderTests {
	private val securityProps = SecurityProps().apply {
		// use random string for jwt signing
		jwt = JwtProps(Crypto.get())
	}
	private val userDetailsService = Mockito.mock(UserDetailsService::class.java)

	private val provider = JwtTokenProvider(securityProps, userDetailsService)

	@Test
	fun `test that username can be retrieved from a request token`() {
		val username = "test.user"
		val token = provider.createRequestToken(username, listOf())

		assertThat(provider.getUsername(token), equalTo(username))
	}

	@Test
	fun `test that username can be retrieved from a refresh token`() {
		val username = "test.user"
		val token = provider.createRefreshToken(username, listOf())

		assertThat(provider.getUsername(token), equalTo(username))
	}

	@Test
	fun `test that fresh token is valid`() {
		val token = provider.createRequestToken("test.user", listOf())
		assertTrue(provider.isTokenValid(token, ""))
	}

	@Test
	fun `token can be extracted from request`() {
		val code = "thisisasecurecode"
		val request = Mockito.mock(HttpServletRequest::class.java)
		Mockito.`when`(request.getHeader(SecurityConstants.authHeader.toLowerCase())).thenReturn("Bearer $code")

		val token = provider.resolveToken(request)
		assertNotNull(token)
		assertEquals(code, token)
	}

}