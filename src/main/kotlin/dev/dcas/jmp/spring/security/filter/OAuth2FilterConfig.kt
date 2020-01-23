/*
 *   This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.dcas.jmp.spring.security.filter

import dev.dcas.jmp.spring.security.oauth2.OAuth2TokenProvider
import org.springframework.security.config.annotation.SecurityConfigurerAdapter
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.DefaultSecurityFilterChain

class OAuth2FilterConfig(private val provider: OAuth2TokenProvider):
    SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity>() {

    override fun configure(builder: HttpSecurity) {
        builder.addFilter(OAuth2Filter(provider))
    }
}