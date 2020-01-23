/*
 *   This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.dcas.jmp.spring.security.rest

import dev.castive.log2.logi
import dev.castive.log2.logv
import dev.dcas.jmp.spring.security.SecurityConstants
import dev.dcas.jmp.spring.security.jwt.JwtTokenProvider
import dev.dcas.jmp.spring.security.model.AuthToken
import dev.dcas.jmp.spring.security.model.BasicAuth
import dev.dcas.jmp.spring.security.model.repo.UserRepository
import dev.dcas.jmp.spring.security.service.JwtService
import dev.dcas.jmp.spring.security.service.LdapService
import dev.dcas.jmp.spring.security.util.BadRequestResponse
import dev.dcas.jmp.spring.security.util.NotFoundResponse
import dev.dcas.jmp.spring.security.util.Responses
import dev.dcas.jmp.spring.security.util.UnauthorizedResponse
import dev.dcas.util.extend.isESNullOrBlank
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/auth")
class AuthControl @Autowired constructor(
    private val userRepo: UserRepository,
    private val ldapService: LdapService,
    private val jwtTokenProvider: JwtTokenProvider,
    private val authService: JwtService,
    private val passwordEncoder: PasswordEncoder
) {
    @PostMapping("/login", produces = [MediaType.APPLICATION_JSON_VALUE], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createToken(@RequestBody basic: BasicAuth): AuthToken {
        // try ldap first
        ldapService.getUserByName(basic)?.let {
            "Located user in LDAP: ${it.username}".logi(javaClass)
            return authService.createToken(it)
        }
        "Attempting to locate user in database: ${basic.username}".logv(javaClass)
        // fallback to a standard database user
        val user = userRepo.findFirstByUsername(basic.username) ?: run {
            "Failed to locate user in database: ${basic.username}".logi(javaClass)
            throw NotFoundResponse(Responses.NOT_FOUND_USER)
        }
        // only allow JWT generation for local users
        if(user.hash.isESNullOrBlank() || user.source != SecurityConstants.sourceLocal)
            throw BadRequestResponse("Cannot generate token for this user")
        if(!passwordEncoder.matches(basic.password, user.hash))
            throw UnauthorizedResponse("Incorrect username or password")
        return authService.createToken(user)
    }

    @GetMapping("/refresh", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun refreshToken(@RequestParam(required = true) refreshToken: String): AuthToken {
        return authService.refreshToken(refreshToken)
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/logout", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun revokeToken(request: HttpServletRequest): ResponseEntity<Nothing> {
        val token = jwtTokenProvider.resolveToken(request) ?: throw UnauthorizedResponse()
        authService.revokeToken(token)
        return ResponseEntity.noContent().build()
    }
}