/*
 *   This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.dcas.jmp.security.shim.entity

import org.springframework.security.core.GrantedAuthority

enum class Role: GrantedAuthority {
	ROLE_ADMIN, ROLE_USER;

	override fun getAuthority(): String = name
}