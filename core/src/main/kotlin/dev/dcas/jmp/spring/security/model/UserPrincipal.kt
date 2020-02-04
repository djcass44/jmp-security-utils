/*
 *   This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.dcas.jmp.spring.security.model

import dev.dcas.jmp.spring.security.model.entity.UserEntity
import org.springframework.security.core.userdetails.User

class UserPrincipal(val dao: UserEntity): User(dao.username, dao.hash ?: "", dao.roles)