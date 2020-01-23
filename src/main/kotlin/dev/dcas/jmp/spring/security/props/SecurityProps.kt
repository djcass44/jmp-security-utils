/*
 *   This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.dcas.jmp.spring.security.props

import dev.dcas.jmp.spring.security.oauth2.ProviderConfig
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.context.annotation.Configuration

@Configuration
@ConstructorBinding
@ConfigurationProperties(prefix = "security")
data class SecurityProps(
    val oauth2: List<ProviderConfig> = listOf(),
    val allowCors: Boolean = false,
    val baseUrl: String = "http://localhost:8080"
)