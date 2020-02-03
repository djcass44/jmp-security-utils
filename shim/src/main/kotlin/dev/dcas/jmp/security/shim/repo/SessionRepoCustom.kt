/*
 *   This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.dcas.jmp.security.shim.repo

import dev.dcas.jmp.security.shim.entity.Session
import dev.dcas.jmp.spring.security.model.entity.UserEntity

interface SessionRepoCustom {
	fun findFirstByUserAndRefreshTokenAndActiveTrue(user: UserEntity, refreshToken: String): Session?
	fun findFirstByUserAndRequestTokenAndActiveTrue(user: UserEntity, requestToken: String): Session?
	fun findFirstByRequestTokenAndActiveTrue(requestToken: String): Session?
}