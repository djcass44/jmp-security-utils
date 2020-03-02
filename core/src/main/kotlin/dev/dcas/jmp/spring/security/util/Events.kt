/*
 *   This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.dcas.jmp.spring.security.util

import dev.castive.log2.logv
import java.util.*

/**
 * This object is used as a centralised way of running code when certain events happen
 * E.g. when a user is created, groups need to be synced
 */
object Events {
	interface Listener {
		fun onUserCreated(source: String, username: String? = null)
	}

	private val listeners = Collections.synchronizedList<Listener>(arrayListOf())

	fun addListener(l: Listener) {
		"Added event listener: ${listeners.add(l)}".logv(javaClass)
	}

	fun removeListener(l: Listener) {
		"Removed event listener: ${listeners.remove(l)}".logv(javaClass)
	}

	// root emitter which fires events to all listeners
	val eventEmitter = object : Listener {
		override fun onUserCreated(source: String, username: String?) {
			"Firing event [onUserCreated] to ${listeners.size} listeners".logv(javaClass)
			listeners.forEach {
				it.onUserCreated(source, username)
			}
		}
	}
}