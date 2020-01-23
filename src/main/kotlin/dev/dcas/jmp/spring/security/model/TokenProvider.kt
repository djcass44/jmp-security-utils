/*
 *   This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.dcas.jmp.spring.security.model

import org.springframework.security.core.Authentication
import javax.servlet.http.HttpServletRequest

interface TokenProvider {
    fun getAuthentication(token: String): Authentication?
    fun isTokenValid(token: String, source: String): Boolean
    fun resolveToken(request: HttpServletRequest): String?
}