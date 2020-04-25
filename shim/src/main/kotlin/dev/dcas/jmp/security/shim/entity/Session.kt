/*
 *   This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.dcas.jmp.security.shim.entity

import dev.dcas.jmp.spring.security.model.entity.SessionEntity
import dev.dcas.util.spring.data.UUIDConverterCompat
import java.util.UUID
import javax.persistence.*

@Entity
@Table(name = "Sessions")
data class Session(
	@Id
	@Convert(converter = UUIDConverterCompat::class)
	override val id: UUID,
	@Column(length=10485760)
	val requestToken: String,
	@Column(length=10485760)
	val refreshToken: String,
	@OneToOne
	val meta: Meta,
	@ManyToOne
	override val user: User,
	var active: Boolean = false
): SessionEntity