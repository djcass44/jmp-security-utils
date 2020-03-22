/*
 *   This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.dcas.jmp.security.shim.util

import dev.castive.log2.loge
import dev.dcas.jmp.security.shim.entity.User
import dev.dcas.jmp.spring.security.model.UserPrincipal
import dev.dcas.util.spring.responses.UnauthorizedResponse
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder

fun SecurityContext.user(): User? = kotlin.runCatching {
	if(SecurityContextHolder.getContext().authentication.principal == "anonymousUser")
		return null
	(SecurityContextHolder.getContext().authentication.principal as UserPrincipal).dao as User
}.onFailure {
	"Failed to extract user from SecurityContextHolder: ${SecurityContextHolder.getContext().authentication.principal}".loge(javaClass, it)
}.getOrNull()

fun SecurityContext.assertUser(): User = user() ?: throw UnauthorizedResponse()