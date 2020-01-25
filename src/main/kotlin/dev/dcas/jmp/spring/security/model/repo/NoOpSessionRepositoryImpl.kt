package dev.dcas.jmp.spring.security.model.repo

import dev.dcas.jmp.spring.security.model.entity.SessionEntity
import dev.dcas.jmp.spring.security.model.entity.UserEntity
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.stereotype.Component

@Component
@ConditionalOnMissingBean(name = ["sessionRepository"])
class NoOpSessionRepositoryImpl: SessionRepository {
	override fun findFirstByRefreshTokenAndActiveTrue(token: String): SessionEntity? = null

	override fun findFirstByRequestTokenAndActiveTrue(token: String): SessionEntity? = null

	override fun findFirstByUserAndRefreshTokenAndActiveTrue(user: UserEntity, refreshToken: String): SessionEntity? = null

	override fun findFirstByUserAndRequestTokenAndActiveTrue(user: UserEntity, requestToken: String): SessionEntity? = null

	override fun disable(session: SessionEntity): SessionEntity = session

	override fun create(requestToken: String, refreshToken: String, user: UserEntity): SessionEntity {
		throw NotImplementedError("This is a no-op bean")
	}

	override fun update(session: SessionEntity): SessionEntity {
		throw NotImplementedError("This is a no-op bean")
	}
}