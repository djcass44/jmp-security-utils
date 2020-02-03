/*
 *   This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.dcas.jmp.spring.security.filter

import dev.castive.log2.logd
import dev.castive.log2.logv
import dev.dcas.jmp.spring.security.SecurityConstants
import dev.dcas.jmp.spring.security.model.TokenProvider
import dev.dcas.util.extend.ellipsize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

abstract class AbstractAuthenticationFilter: OncePerRequestFilter() {

    fun filter(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
        provider: TokenProvider
    ) {
        val source: String? = request.getHeader(SecurityConstants.sourceHeader)
        if(!isRelevantRequest(source)) {
	        "Skipping filter for source: $source (not relevant)".logd(javaClass)
            // this is not for us to mess with
            filterChain.doFilter(request, response)
            return
        }
	    "Accepting filter for source: $source".logd(javaClass)
        val token = provider.resolveToken(request)
        if(token != null && provider.isTokenValid(token, source!!)) {
            val auth = provider.getAuthentication(token)
            if(auth != null) {
                "Located user principal: ${auth.name} with roles: ${auth.authorities.size}".logv(javaClass)
                SecurityContextHolder.getContext().authentication = auth
            }
            else {
	            "Failed to locate authentication for valid token: $source".logv(javaClass)
	            SecurityContextHolder.clearContext()
            }
        }
        else {
            "Failed to parse token: ${token?.ellipsize(24)}".logd(javaClass)
            // ensure context is cleared
            SecurityContextHolder.clearContext()
        }
        // continue with the request
        filterChain.doFilter(request, response)
    }

    abstract fun isRelevantRequest(source: String?): Boolean
}