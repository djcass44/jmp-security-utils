/*
 *   This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.dcas.jmp.spring.security.util

import dev.dcas.util.extend.base64
import java.security.MessageDigest

/**
 * Check whether a hashed value matches a raw string
 * Hashes the raw string and compares it to the hashed one
 * @param rawText: non-hashed string
 * @param hash: hashed string to compare
 * @return true if the hash of `rawText` matches `hash`
 */
fun MessageDigest.matches(rawText: String, hash: String): Boolean {
	return encode(rawText) == hash
}

fun MessageDigest.encode(rawText: String): String {
	return digest(rawText.toByteArray()).base64()
}