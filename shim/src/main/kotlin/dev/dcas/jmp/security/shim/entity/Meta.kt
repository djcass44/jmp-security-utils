/*
 *   This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.dcas.jmp.security.shim.entity

import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class Meta(
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	val id: Long,
	val created: Long = System.currentTimeMillis(),
	var edited: Long = System.currentTimeMillis(),
	val createdBy: UUID,
	var editedBy: UUID
) {
	companion object {
		fun fromUser(id: UUID): Meta = Meta(0, createdBy = id, editedBy = id)
		fun fromUser(user: User): Meta = Meta(0, createdBy = user.id, editedBy = user.id)
	}

	fun onUpdate(user: User): Meta = apply {
		edited = System.currentTimeMillis()
		editedBy = user.id
	}
}