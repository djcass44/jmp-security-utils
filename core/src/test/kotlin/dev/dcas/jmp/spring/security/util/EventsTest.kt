/*
 *   This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.dcas.jmp.spring.security.util

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.ArgumentMatchers.isNull
import org.mockito.Mockito.*

class EventsTest {
	private val events = Events()

	@BeforeEach
	internal fun setUp() {
		events.reset()
	}

	@Test
	fun `added listener can hear events`() {
		val l = spy(object : Events.Listener {
			override fun onUserCreated(source: String, username: String?) {}

		})

		events.addListener(l)
		events.emit.onUserCreated("test")
		events.emit.onUserCreated("test", "test.user")
		verify(l, times(1)).onUserCreated(anyString(), isNull())
		verify(l, times(1)).onUserCreated(anyString(), anyString())
	}

	@Test
	fun `removed listener cannnot hear events`() {
		val l = spy(object : Events.Listener {
			override fun onUserCreated(source: String, username: String?) {}

		})

		events.addListener(l)
		events.emit.onUserCreated("test")
		events.removeListener(l)
		events.emit.onUserCreated("test", "test.user")
		verify(l, times(1)).onUserCreated(anyString(), isNull())
		verify(l, times(0)).onUserCreated(anyString(), anyString())
	}
}