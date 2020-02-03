/*
 *   This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.dcas.jmp.spring.security.filter

import dev.dcas.jmp.spring.security.SecurityConstants
import dev.dcas.jmp.spring.security.oauth2.OAuth2TokenProvider
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class OAuth2Filter(private val oauth2TokenProvider: OAuth2TokenProvider): AbstractAuthenticationFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        super.filter(request, response, filterChain, oauth2TokenProvider)
    }

    override fun isRelevantRequest(source: String?): Boolean = source != null && source.startsWith(SecurityConstants.sourceOAuth2)
}