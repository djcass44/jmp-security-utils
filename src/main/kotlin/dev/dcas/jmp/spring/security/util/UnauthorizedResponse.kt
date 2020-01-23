/*
 *   This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.dcas.jmp.spring.security.util

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class UnauthorizedResponse(message: String = "Unauthorized."): ResponseStatusException(HttpStatus.UNAUTHORIZED, message)