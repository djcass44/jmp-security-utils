/*
 *   This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.dcas.jmp.spring.security.props

import org.springframework.boot.context.properties.ConstructorBinding
import java.util.concurrent.TimeUnit

@ConstructorBinding
data class JwtProps(
    val secretKey: String,
    val requestLimit: Long = TimeUnit.HOURS.toMillis(1),
    val refreshLimit: Long = TimeUnit.HOURS.toMillis(8),
    val leeway: Long = TimeUnit.HOURS.toMillis(1)
)