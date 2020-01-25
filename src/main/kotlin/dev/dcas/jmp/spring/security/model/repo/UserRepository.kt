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

package dev.dcas.jmp.spring.security.model.repo

import dev.dcas.jmp.spring.security.model.UserProjection
import dev.dcas.jmp.spring.security.model.entity.UserEntity

interface UserRepository {
    fun findFirstByUsername(username: String): UserEntity?
    fun findFirstByUsernameAndSource(username: String, source: String): UserEntity?
    fun existsByUsername(username: String): Boolean
    fun update(data: UserProjection): UserEntity

    fun createWithData(source: String, username: String, data: UserProjection): UserEntity
}