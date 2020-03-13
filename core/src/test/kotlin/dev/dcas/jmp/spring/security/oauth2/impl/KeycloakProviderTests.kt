/*
 *   This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.dcas.jmp.spring.security.oauth2.impl

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import dev.dcas.jmp.spring.security.TestUtils
import dev.dcas.jmp.spring.security.client.KeycloakApiClient
import dev.dcas.jmp.spring.security.oauth2.ProviderConfig
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import java.net.URI
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class KeycloakProviderTests {
	private val config = ProviderConfig().apply {
		name = "keycloak"
		enabled = true
		apiUrl = "http://localhost:8080"
		callbackUrl = "http://localhost:3000/callback"
		scope = "master" // actually realm
		clientId = "abcde12345fghij67890"
		clientSecret = "mysecretappkey"
	}
	private val client = Mockito.mock(KeycloakApiClient::class.java)

	private val provider = KeycloakProvider(config, client)

	private val objectMapper = jacksonObjectMapper()

	@ParameterizedTest
	@ValueSource(strings = [
		"",
		"  ",
		"\n",
		"test",
		"mysecretaccesscode"
	])
	fun `isTokenValid always returns true`(token: String) {
		assertTrue(provider.isTokenValid(token))
	}

	@Disabled
	@Test
	fun `test that user data can be loaded`() {
		Mockito.`when`(
			client.getUser(any(URI::class.java), anyString(), anyString())
		).thenReturn(
			objectMapper.readValue(TestUtils.loadFixture("keycloak_current_user.json"), KeycloakProvider.KeycloakUser::class.java)
		)

		val user = provider.getUserInformation("abcdefgh12345678")
		assertNotNull(user)
		assertThat(user.username, equalTo("jane.doe"))
		assertThat(user.source, equalTo("keycloak"))
	}
}