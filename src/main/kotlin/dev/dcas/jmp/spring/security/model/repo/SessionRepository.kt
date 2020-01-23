/*
 *   This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

/*
 *   This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.dcas.jmp.spring.security.model.repo

import dev.dcas.jmp.spring.security.jwt.JwtTokenProvider
import dev.dcas.jmp.spring.security.model.entity.SessionEntity
import dev.dcas.jmp.spring.security.model.entity.UserEntity
import org.springframework.security.crypto.password.PasswordEncoder

interface SessionRepository {
    fun findFirstByRefreshTokenAndActiveTrue(token: String): SessionEntity?
    fun findFirstByRequestTokenAndActiveTrue(token: String): SessionEntity?
    fun findFirstByUserAndRefreshTokenAndActiveTrue(user: UserEntity, refreshToken: String): SessionEntity?
    fun findFirstByUserAndRequestTokenAndActiveTrue(user: UserEntity, requestToken: String): SessionEntity?

    fun disable(session: SessionEntity): SessionEntity
    fun create(requestToken: String, refreshToken: String, user: UserEntity): SessionEntity
    fun create(user: UserEntity, provider: JwtTokenProvider, passwordEncoder: PasswordEncoder): Triple<SessionEntity, String, String>
    fun update(session: SessionEntity): SessionEntity
}