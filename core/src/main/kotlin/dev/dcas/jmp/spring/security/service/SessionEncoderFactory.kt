/*
 *   This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.dcas.jmp.spring.security.service

import dev.dcas.jmp.spring.security.SecurityConstants
import dev.dcas.jmp.spring.security.props.SecurityProps
import org.springframework.stereotype.Service
import java.security.MessageDigest

@Service
class SessionEncoderFactory(
	private val securityConfig: SecurityProps
) {
	/**
	 * Generates a new session encoder
	 * @return MessageDigest or null is session hashing is disabled
	 */
	fun newSessionEncoder(): MessageDigest? = if(securityConfig.hashSessions)
		MessageDigest.getInstance(SecurityConstants.DIGEST_SHA3)
	else
		null
}