/*
 *   This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.dcas.jmp.spring.security.aop

import dev.dcas.jmp.spring.security.config.Beans
import dev.dcas.jmp.spring.security.config.JwtSecurityConfig
import dev.dcas.jmp.spring.security.config.WebSecurityConfig
import dev.dcas.jmp.spring.security.jwt.JwtTokenProvider
import dev.dcas.jmp.spring.security.model.repo.NoOpGroupRepositoryImpl
import dev.dcas.jmp.spring.security.model.repo.NoOpSessionRepositoryImpl
import dev.dcas.jmp.spring.security.model.repo.NoOpUserRepositoryImpl
import dev.dcas.jmp.spring.security.props.SecurityProps
import dev.dcas.jmp.spring.security.rest.AuthControl
import dev.dcas.jmp.spring.security.service.JwtService
import dev.dcas.jmp.spring.security.service.LdapService
import dev.dcas.jmp.spring.security.service.UserDetailsService
import org.springframework.context.annotation.Import

@Import(
	WebSecurityConfig::class,
	Beans::class,
	AuthControl::class,
	JwtSecurityConfig::class,
	LdapService::class,
	NoOpUserRepositoryImpl::class,
	NoOpGroupRepositoryImpl::class,
	NoOpSessionRepositoryImpl::class,
	JwtTokenProvider::class,
	SecurityProps::class,
	UserDetailsService::class,
	JwtService::class,
	LdapService::class
)
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class EnableJwt