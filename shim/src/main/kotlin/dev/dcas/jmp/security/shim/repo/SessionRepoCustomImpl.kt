/*
 *   This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.dcas.jmp.security.shim.repo

import dev.dcas.jmp.security.shim.entity.Session
import dev.dcas.jmp.security.shim.entity.User
import dev.dcas.jmp.spring.security.model.entity.UserEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional(readOnly = true)
class SessionRepoCustomImpl @Autowired constructor(
	private val sessionRepo: SessionRepo,
	private val passwordEncoder: PasswordEncoder
): SessionRepoCustom {
	override fun findFirstByUserAndRefreshTokenAndActiveTrue(user: UserEntity, refreshToken: String): Session? {
		val sessions = sessionRepo.findAllByUserAndActiveIsTrue(user as User)
		return sessions.firstOrNull {
			passwordEncoder.matches(refreshToken, it.refreshToken)
		}
	}

	override fun findFirstByUserAndRequestTokenAndActiveTrue(user: UserEntity, requestToken: String): Session? {
		val sessions = sessionRepo.findAllByUserAndActiveIsTrue(user as User)
		return sessions.firstOrNull {
			passwordEncoder.matches(requestToken, it.requestToken)
		}
	}

	override fun findFirstByRequestTokenAndActiveTrue(requestToken: String): Session? {
		val sessions = sessionRepo.findAllByActiveTrue()
		return sessions.firstOrNull {
			kotlin.runCatching {
				passwordEncoder.matches(requestToken, it.requestToken)
			}.getOrDefault(false)
		}
	}
}