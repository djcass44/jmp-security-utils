/*
 *   This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.dcas.jmp.spring.security.filter

import dev.dcas.jmp.spring.security.SecurityConstants
import dev.dcas.jmp.spring.security.jwt.JwtTokenProvider

class JwtFilter(private val provider: JwtTokenProvider): AbstractAuthenticationFilter() {
    override fun doFilterInternal(
        request: javax.servlet.http.HttpServletRequest,
        response: javax.servlet.http.HttpServletResponse,
        filterChain: javax.servlet.FilterChain
    ) {
        super.filter(request, response, filterChain, provider)
    }

    override fun isRelevantRequest(source: String?): Boolean = source != null && !(source.startsWith(SecurityConstants.sourceLdap) || source.startsWith(SecurityConstants.sourceLocal))
}