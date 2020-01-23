/*
 *   This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.dcas.jmp.spring.security.aop

import dev.dcas.jmp.spring.security.config.OAuth2SecurityConfig
import dev.dcas.jmp.spring.security.rest.OAuth2Control
import org.springframework.context.annotation.Import

@Import(OAuth2Control::class, OAuth2SecurityConfig::class)
@Target
@Retention(AnnotationRetention.RUNTIME)
annotation class EnableOAuth2