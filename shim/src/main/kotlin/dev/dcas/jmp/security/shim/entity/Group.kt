/*
 *   This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.dcas.jmp.security.shim.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import dev.dcas.jmp.spring.security.model.entity.GroupEntity
import dev.dcas.util.spring.data.UUIDConverterCompat
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "Groups")
data class Group(
	@Id
	@Convert(converter = UUIDConverterCompat::class)
	val id: UUID = UUID.randomUUID(),
	var name: String,
	val source: String,
	var public: Boolean = false,
	var defaultFor: String? = null,
	@JsonIgnore
	@ManyToMany
	val users: MutableSet<User> = mutableSetOf()
): GroupEntity {
	fun containsUser(user: User): Boolean = users.any {
		it.id == user.id
	}
}