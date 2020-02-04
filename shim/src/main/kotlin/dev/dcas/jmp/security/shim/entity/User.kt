/*
 *   This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.dcas.jmp.security.shim.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import dev.castive.jmp.entity.Role
import dev.castive.log2.logv
import dev.dcas.jmp.spring.security.model.entity.UserEntity
import dev.dcas.util.spring.data.UUIDConverterCompat
import org.springframework.security.core.GrantedAuthority
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "Users")
data class User(
	@Id
	@Convert(converter = UUIDConverterCompat::class)
	override val id: UUID = UUID.randomUUID(),
	@Column(unique = true, nullable = false)
	override val username: String,
	var displayName: String,
	var avatarUrl: String? = null,
	@JsonIgnore
	override val hash: String? = null,
	@ElementCollection(fetch = FetchType.EAGER)
	override val roles: MutableList<GrantedAuthority> = mutableListOf(Role.ROLE_USER),
	@OneToOne
	val meta: Meta,
	override val source: String
): UserEntity {
	fun isAdmin(): Boolean = roles.contains(Role.ROLE_ADMIN)

	fun addRole(role: Role) {
		// only add the role if it isn't already there
		if(!roles.contains(role)) {
			"Adding role ${role.name} to user $username".logv(javaClass)
			roles.add(role)
		}
	}
}
