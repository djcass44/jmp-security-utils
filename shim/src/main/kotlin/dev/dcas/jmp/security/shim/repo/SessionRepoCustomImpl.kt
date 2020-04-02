/*
 *   This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.dcas.jmp.security.shim.repo

import dev.castive.log2.logi
import dev.dcas.jmp.security.shim.entity.Session
import dev.dcas.jmp.security.shim.entity.User
import dev.dcas.jmp.spring.security.SecurityConstants
import dev.dcas.jmp.spring.security.model.entity.UserEntity
import dev.dcas.jmp.spring.security.props.SecurityProps
import dev.dcas.jmp.spring.security.util.matches
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.security.MessageDigest
import javax.annotation.PostConstruct

@Repository
@Transactional(readOnly = true)
class SessionRepoCustomImpl(
	private val sessionRepo: SessionRepo,
	private val securityProps: SecurityProps
): SessionRepoCustom {

	@PostConstruct
	fun init() {
		"Session hashing: ${securityProps.hashSessions}".logi(javaClass)
	}

	override fun findFirstByUserAndRefreshTokenAndActiveTrue(user: UserEntity, refreshToken: String): Session? {
		val sessionEncoder = sessionEncoder()
		val sessions = sessionRepo.findAllByUserAndActiveIsTrue(user as User)
		return sessions.firstOrNull {
			sessionEncoder?.matches(refreshToken, it.refreshToken) ?: refreshToken == it.refreshToken
		}
	}

	override fun findFirstByUserAndRequestTokenAndActiveTrue(user: UserEntity, requestToken: String): Session? {
		val sessionEncoder = sessionEncoder()
		val sessions = sessionRepo.findAllByUserAndActiveIsTrue(user as User)
		return sessions.firstOrNull {
			sessionEncoder?.matches(requestToken, it.requestToken) ?: requestToken == it.requestToken
		}
	}

	override fun findFirstByRequestTokenAndActiveTrue(requestToken: String): Session? {
		val sessionEncoder = sessionEncoder()
		val sessions = sessionRepo.findAllByActiveTrue()
		return sessions.firstOrNull {
			kotlin.runCatching {
				sessionEncoder?.matches(requestToken, it.requestToken) ?: requestToken == it.requestToken
			}.getOrDefault(false)
		}
	}

	private fun sessionEncoder(): MessageDigest? = if(securityProps.hashSessions) SecurityConstants.getSessionEncoder() else null
}