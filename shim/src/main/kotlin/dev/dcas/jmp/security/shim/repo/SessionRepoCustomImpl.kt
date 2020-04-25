/*
 *   This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.dcas.jmp.security.shim.repo

import dev.castive.log2.logi
import dev.castive.log2.logv
import dev.dcas.jmp.security.shim.entity.Session
import dev.dcas.jmp.security.shim.entity.User
import dev.dcas.jmp.spring.security.model.entity.UserEntity
import dev.dcas.jmp.spring.security.props.SecurityProps
import dev.dcas.jmp.spring.security.service.SessionEncoderFactory
import dev.dcas.jmp.spring.security.util.matches
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import javax.annotation.PostConstruct

@Repository
@Transactional(readOnly = true)
class SessionRepoCustomImpl(
	private val sessionRepo: SessionRepo,
	private val securityProps: SecurityProps,
	private val sessionEncoderFactory: SessionEncoderFactory
): SessionRepoCustom {

	@PostConstruct
	fun init() {
		"Session hashing: ${securityProps.hashSessions}".logi(javaClass)
	}

	override fun findFirstByUserAndRefreshTokenAndActiveTrue(user: UserEntity, refreshToken: String): Session? {
		val sessions = sessionRepo.findAllByUserAndActiveIsTrue(user as User)
		return sessions.firstOrNull {
			return@firstOrNull checkTokens(refreshToken, it.refreshToken)
		}
	}

	override fun findFirstByUserAndRequestTokenAndActiveTrue(user: UserEntity, requestToken: String): Session? {
		val sessions = sessionRepo.findAllByUserAndActiveIsTrue(user as User)
		return sessions.firstOrNull {
			return@firstOrNull checkTokens(requestToken, it.requestToken)
		}
	}

	override fun findFirstByRequestTokenAndActiveTrue(requestToken: String): Session? {
		val sessions = sessionRepo.findAllByActiveIsTrue()
		"Found ${sessions.size} active sessions".logv(javaClass)
		return sessions.firstOrNull {
			return@firstOrNull checkTokens(requestToken, it.requestToken)
		}
	}

	/**
	 * Check whether 2 tokens are equal, account for the possibility that they may be hashed
	 * This is a weird hack because encoder?.matches ?: token == token2 would always return false
	 */
	private fun checkTokens(token: String, token2: String): Boolean {
		val matches = sessionEncoderFactory.newSessionEncoder()?.matches(token, token2)
		val matchesFallback = token == token2
		return matches ?: matchesFallback
	}
}