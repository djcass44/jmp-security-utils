/*
 *   This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.dcas.jmp.spring.security.oauth2

data class ProviderConfig(
    val name: String,
    val enabled:Boolean = false,
    val apiUrl: String,
    val callbackUrl: String,
    val scope: String,
    val clientId: String,
    val clientSecret: String
)