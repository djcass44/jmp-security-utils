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

import dev.dcas.jmp.spring.security.props.SecurityProps
import dev.dcas.jmp.spring.security.service.SessionEncoderFactory
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

class MessageDigestExtensionTest {

	private val digestFactory = SessionEncoderFactory(SecurityProps().apply {
		hashSessions = true
	})

	@Test
	fun `hashed value matches`() {
		val rawText = "This is a test!"

		val hash = digestFactory.newSessionEncoder()!!.encode(rawText)
		assertThat(digestFactory.newSessionEncoder()!!.matches(rawText, hash), equalTo(true))
	}
}