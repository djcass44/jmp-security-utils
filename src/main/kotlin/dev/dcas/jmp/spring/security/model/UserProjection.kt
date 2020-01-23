/*
 *   This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.dcas.jmp.spring.security.model

data class UserProjection(
    val username: String,
    val displayName: String,
    val avatarUrl: String,
    val source: String
)

interface OAuth2User {
    fun project(): UserProjection
}