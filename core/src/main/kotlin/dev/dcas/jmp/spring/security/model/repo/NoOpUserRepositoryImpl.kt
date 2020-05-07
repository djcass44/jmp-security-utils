package dev.dcas.jmp.spring.security.model.repo

import dev.dcas.jmp.spring.security.model.UserProjection
import dev.dcas.jmp.spring.security.model.entity.UserEntity
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.stereotype.Component

@Component
@ConditionalOnMissingBean(UserRepository::class)
class NoOpUserRepositoryImpl: UserRepository {
    override fun findFirstByUsername(username: String): UserEntity? = null

    override fun findFirstByUsernameAndSource(username: String, source: String): UserEntity? = null

    override fun existsByUsername(username: String): Boolean = false

	override fun existsByUsernameAndSource(username: String, source: String): Boolean = false

	override fun update(username: String, data: UserProjection): UserEntity {
        throw NotImplementedError("This is a no-op bean")
    }

    override fun createWithData(source: String, username: String, data: UserProjection): UserEntity {
        throw NotImplementedError("This is a no-op bean")
    }

}