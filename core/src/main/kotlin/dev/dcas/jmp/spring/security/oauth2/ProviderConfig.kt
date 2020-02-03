/*
 *   This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.dcas.jmp.spring.security.oauth2

class ProviderConfig {
	lateinit var name: String
	var enabled: Boolean = false
	lateinit var apiUrl: String
	lateinit var callbackUrl: String
	lateinit var scope: String
	lateinit var clientId: String
	lateinit var clientSecret: String
}