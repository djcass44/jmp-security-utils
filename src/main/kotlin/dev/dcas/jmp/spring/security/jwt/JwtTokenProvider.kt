/*
 *   This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.dcas.jmp.spring.security.jwt

import dev.castive.log2.loge
import dev.dcas.jmp.spring.security.SecurityConstants
import dev.dcas.jmp.spring.security.model.TokenProvider
import dev.dcas.jmp.spring.security.props.JwtProps
import dev.dcas.jmp.spring.security.service.UserDetailsService
import dev.dcas.util.extend.ellipsize
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import java.util.*
import javax.servlet.http.HttpServletRequest

@Component
class JwtTokenProvider @Autowired constructor(
    private val jwtProps: JwtProps,
    private val userDetailsService: UserDetailsService
): TokenProvider {
    fun createRequestToken(username: String, roles: List<GrantedAuthority>): String = createToken(username, roles, jwtProps.requestLimit)
    fun createRefreshToken(username: String, roles: List<GrantedAuthority>): String = createToken(username, roles, jwtProps.refreshLimit)

    private fun createToken(username: String, roles: List<GrantedAuthority>, limit: Long): String {
        val claims = Jwts.claims()
            .setSubject(username)
        claims["auth"] = roles.mapNotNull {
            SimpleGrantedAuthority(it.authority)
        }
        val now = Date()
        val exp = Date(now.time + limit)

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(exp)
            .signWith(SignatureAlgorithm.HS256, jwtProps.secretKey)
            .compact()
    }

    override fun getAuthentication(token: String): Authentication? {
        val user = userDetailsService.loadUserByUsername(getUsername(token) ?: return null) ?: return null
        return UsernamePasswordAuthenticationToken(user, "", user.authorities)
    }

    fun getUsername(token: String): String? = kotlin.runCatching {
        Jwts.parser().setSigningKey(jwtProps.secretKey).parseClaimsJws(token).body.subject
    }.onFailure {
        "Encountered expired or invalid Jwt token: ${token.ellipsize(24)}".loge(javaClass, it)
    }.getOrNull()

    override fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader(SecurityConstants.authHeader.toLowerCase()) ?: return null
        if(bearerToken.startsWith("Bearer "))
            return bearerToken.substring(7)
        return null
    }

    override fun isTokenValid(token: String, source: String): Boolean = kotlin.runCatching {
        Jwts.parser()
            .setSigningKey(jwtProps.secretKey)
            .setAllowedClockSkewSeconds(jwtProps.leeway / 1000)
            .parseClaimsJws(token)
        true
    }.onFailure {
        "Encountered expired or invalid Jwt token: ${token.ellipsize(24)}".loge(javaClass, it)
    }.getOrDefault(false)
}