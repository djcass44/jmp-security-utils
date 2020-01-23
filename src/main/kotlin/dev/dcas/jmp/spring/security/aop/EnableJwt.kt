/*
 *   This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.dcas.jmp.spring.security.aop

import dev.dcas.jmp.spring.security.config.JwtSecurityConfig
import dev.dcas.jmp.spring.security.rest.AuthControl
import org.springframework.context.annotation.Import

@Import(AuthControl::class, JwtSecurityConfig::class)
@Target
@Retention(AnnotationRetention.RUNTIME)
annotation class EnableJwt