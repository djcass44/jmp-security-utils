/*
 *   This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.dcas.jmp.spring.security

import java.security.MessageDigest

object SecurityConstants {
    const val sourceHeader = "X-Auth-Source"
    const val authHeader = "Authorization"

    const val sourceLocal = "local"
    const val sourceLdap = "ldap"
    const val sourceOAuth2 = "oauth2"

	private const val DIGEST_SHA3 = "SHA3-384"

	fun getSessionEncoder(): MessageDigest = MessageDigest.getInstance(DIGEST_SHA3)
}