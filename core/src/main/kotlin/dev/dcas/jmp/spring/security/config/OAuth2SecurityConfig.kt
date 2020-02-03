/*
 *   This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.dcas.jmp.spring.security.config

import dev.dcas.jmp.spring.security.filter.OAuth2FilterConfig
import dev.dcas.jmp.spring.security.oauth2.OAuth2TokenProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity

@Configuration
class OAuth2SecurityConfig @Autowired constructor(
    private val oauth2Provider: OAuth2TokenProvider
): SecurityConfigurerAdapter {

    override fun configure(http: HttpSecurity) {
        // apply oauth2 filter
        http.apply(OAuth2FilterConfig(oauth2Provider))
    }
}