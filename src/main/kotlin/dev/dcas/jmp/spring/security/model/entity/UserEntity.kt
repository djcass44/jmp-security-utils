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

package dev.dcas.jmp.spring.security.model.entity

import org.springframework.security.core.GrantedAuthority
import java.util.*

interface UserEntity {
	val id: UUID
    val username: String
    val source: String
    val hash: String?
    val roles: MutableList<GrantedAuthority>
}