/*
 *   This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.dcas.jmp.spring.security.config

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import feign.Logger
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import java.security.MessageDigest

@Configuration
class Beans {
	@ConditionalOnMissingBean(PasswordEncoder::class)
	@Bean
	fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

	@ConditionalOnMissingBean(MessageDigest::class)
	@Bean
	fun sessionEncoder(): MessageDigest = MessageDigest.getInstance("SHA3-384")

	@ConditionalOnMissingBean(ObjectMapper::class)
	@Bean
	fun objectMapper(): ObjectMapper = jacksonObjectMapper().apply {
		disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
	}

	@ConditionalOnMissingBean
	@Bean
	fun feignLoggingLevel(): Logger.Level = Logger.Level.BASIC
}