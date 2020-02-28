/*
 *   This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.dcas.jmp.security.shim.repo

import dev.dcas.jmp.security.shim.entity.Group
import dev.dcas.jmp.security.shim.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface GroupRepo: JpaRepository<Group, UUID> {
	fun findFirstByName(name: String): Group?
	fun findAllByPublicIsTrue(): List<Group>

	@Query("FROM Group g WHERE :user MEMBER g.users")
	fun findAllByUsersIsContaining(user: User): List<Group>
}