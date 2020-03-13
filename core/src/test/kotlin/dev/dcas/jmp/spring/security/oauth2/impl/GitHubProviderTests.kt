/*
 *   This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.dcas.jmp.spring.security.oauth2.impl

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import dev.dcas.jmp.spring.security.TestUtils
import dev.dcas.jmp.spring.security.client.GitHubApiClient
import dev.dcas.jmp.spring.security.oauth2.ProviderConfig
import feign.Response
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import org.springframework.http.HttpStatus
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GitHubProviderTests {
	private val config = ProviderConfig().apply {
		name = "github"
		enabled = true
		callbackUrl = "http://localhost:3000/callback"
		scope = "read:user"
		clientId = "abcde12345fghij67890"
		clientSecret = "mysecretappkey"
	}
	private val client = Mockito.mock(GitHubApiClient::class.java)

	private val provider = GitHubProvider(config, client)

	private val objectMapper = jacksonObjectMapper()


	/**
	 * Disabled because arg 0 of client::isTokenValid is always null
	 */
	@Disabled
	@Test
	fun `test that valid token response returns true`() {
		Mockito.`when`(
			client.isTokenValid(ArgumentMatchers.any(GitHubApiClient.GitHub3Authentication::class.java), anyString(), anyString())
		).thenReturn(
			Response.builder().body(TestUtils.loadFixture("github_token_valid.json").toByteArray()).status(HttpStatus.OK.value()).build()
		)

		val valid = provider.isTokenValid("abcdefgh12345678")
		assertTrue(valid)
	}

	@Test
	fun `test that user data can be loaded`() {
		Mockito.`when`(
			client.getUser(anyString())
		).thenReturn(
			objectMapper.readValue(TestUtils.loadFixture("github_current_user.json"), GitHubProvider.GitHubUser::class.java)
		)

		val user = provider.getUserInformation("abcdefgh12345678")
		assertNotNull(user)
		assertThat(user.username, equalTo("octocat"))
		assertThat(user.source, equalTo("github"))
	}
}