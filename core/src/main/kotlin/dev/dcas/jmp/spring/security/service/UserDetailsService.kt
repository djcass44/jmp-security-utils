/*
 *   This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.dcas.jmp.spring.security.service

import dev.castive.log2.loge
import dev.dcas.jmp.spring.security.model.UserPrincipal
import dev.dcas.jmp.spring.security.model.repo.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserDetailsService @Autowired constructor(
    private val userRepo: UserRepository
): UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails? {
        val user = userRepo.findFirstByUsername(username) ?: run {
            "Found valid claim for non-existent user: $username".loge(javaClass, UsernameNotFoundException("User '$username' not found"))
            return null
        }
        return UserPrincipal(user)
    }
}