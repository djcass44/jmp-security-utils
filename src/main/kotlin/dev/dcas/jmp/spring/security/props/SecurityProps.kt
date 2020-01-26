/*
 *   This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.dcas.jmp.spring.security.props

import dev.dcas.jmp.spring.security.oauth2.ProviderConfig
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "security")
class SecurityProps {
	var oauth2: List<ProviderConfig> = listOf()
	var allowCors: Boolean = false
	var jwt: JwtProps = JwtProps("")
}