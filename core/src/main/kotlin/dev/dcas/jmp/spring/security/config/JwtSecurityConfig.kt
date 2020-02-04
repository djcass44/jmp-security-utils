/*
 *   This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.dcas.jmp.spring.security.config

import dev.dcas.jmp.spring.security.filter.JwtFilterConfig
import dev.dcas.jmp.spring.security.jwt.JwtTokenProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity

@Configuration
class JwtSecurityConfig @Autowired constructor(
    private val jwtProvider: JwtTokenProvider
): SecurityConfigurerAdapter {

    override fun configure(http: HttpSecurity) {
        // apply jwt filter
        http.apply(JwtFilterConfig(jwtProvider))
    }
}