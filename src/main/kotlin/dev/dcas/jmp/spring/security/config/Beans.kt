/*
 *   This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.dcas.jmp.spring.security.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@ConditionalOnMissingBean(PasswordEncoder::class)
@Bean
fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

@ConditionalOnMissingBean(ObjectMapper::class)
@Bean
fun objectMapper(): ObjectMapper = jacksonObjectMapper()