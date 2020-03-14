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

package dev.dcas.jmp.spring.security.util

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import java.security.MessageDigest

class MessageDigestExtensionTest {

	private val digest = MessageDigest.getInstance("SHA3-384")

	@Test
	fun `hashed value matches`() {
		val rawText = "This is a test!"

		val hash = String(digest.digest(rawText.toByteArray()))
		assertThat(digest.matches(rawText, hash), equalTo(true))
	}
}