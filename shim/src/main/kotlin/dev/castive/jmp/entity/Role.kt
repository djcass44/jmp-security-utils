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

package dev.castive.jmp.entity

import org.springframework.security.core.GrantedAuthority

/**
 * This class is located in this package for compat with the current JMP database schema
 * For some reason hibernate is expecting the Role class to be `dev.castive.jmp.entity.Role` and fails if it's elsewhere
 */
enum class Role: GrantedAuthority {
	ROLE_ADMIN, ROLE_USER;

	override fun getAuthority(): String = name
}