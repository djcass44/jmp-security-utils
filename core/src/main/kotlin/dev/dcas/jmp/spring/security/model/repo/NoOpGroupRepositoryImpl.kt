package dev.dcas.jmp.spring.security.model.repo

import dev.dcas.jmp.spring.security.model.entity.GroupEntity
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.stereotype.Component

@Component
@ConditionalOnMissingBean(GroupRepository::class)
class NoOpGroupRepositoryImpl: GroupRepository {
    override fun findFirstByName(name: String): GroupEntity? = null

    override fun create(name: String, source: String, defaultFor: String?) {
        // do nothing
    }
}