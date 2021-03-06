package dev.dcas.jmp.security.shim.impl

import dev.dcas.jmp.security.shim.entity.Meta
import dev.dcas.jmp.security.shim.entity.Session
import dev.dcas.jmp.security.shim.entity.User
import dev.dcas.jmp.security.shim.repo.MetaRepo
import dev.dcas.jmp.security.shim.repo.SessionRepo
import dev.dcas.jmp.security.shim.repo.SessionRepoCustom
import dev.dcas.jmp.spring.security.model.entity.SessionEntity
import dev.dcas.jmp.spring.security.model.entity.UserEntity
import dev.dcas.jmp.spring.security.model.repo.SessionRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class SessionRepository(
	private val sessionRepo: SessionRepo,
	private val sessionRepoCustom: SessionRepoCustom,
	private val metaRepo: MetaRepo
): SessionRepository {
	override fun create(requestToken: String, refreshToken: String, user: UserEntity): SessionEntity {
		val id = UUID.randomUUID()
		val meta = metaRepo.save(Meta.fromUser(user.id))
		return sessionRepo.save(
			Session(
				id,
				requestToken,
				refreshToken,
				meta,
				user as User,
				true
			)
		)
	}

	override fun disable(session: SessionEntity): SessionEntity = sessionRepo.save((session as Session).apply {
		active = false
	})

	override fun findFirstByRefreshTokenAndActiveTrue(token: String): SessionEntity? {
		return sessionRepo.findFirstByRefreshTokenAndActiveTrue(token)
	}

	override fun findFirstByRequestTokenAndActiveTrue(token: String): SessionEntity? {
		return sessionRepoCustom.findFirstByRequestTokenAndActiveTrue(token)
	}

	override fun findFirstByUserAndRefreshTokenAndActiveTrue(user: UserEntity, refreshToken: String): SessionEntity? {
		return sessionRepoCustom.findFirstByUserAndRequestTokenAndActiveTrue(user, refreshToken)
	}

	override fun findFirstByUserAndRequestTokenAndActiveTrue(user: UserEntity, requestToken: String): SessionEntity? {
		return sessionRepoCustom.findFirstByUserAndRequestTokenAndActiveTrue(user, requestToken)
	}

	override fun update(session: SessionEntity): SessionEntity = sessionRepo.save(session as Session)
}
