/*
 *   This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.dcas.jmp.security.shim.repo

import dev.dcas.jmp.security.shim.entity.Session
import dev.dcas.jmp.security.shim.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID
import javax.transaction.Transactional

@Transactional
@Repository
interface SessionRepo: JpaRepository<Session, UUID> {
	fun findAllByUserAndActiveIsTrue(user: User): List<Session>
	fun findFirstByRefreshTokenAndActiveTrue(refreshToken: String): Session?
	fun findAllByActiveIsTrue(): List<Session>
}