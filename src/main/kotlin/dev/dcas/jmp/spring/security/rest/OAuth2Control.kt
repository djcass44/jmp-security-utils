/*
 *   This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.dcas.jmp.spring.security.rest

import dev.castive.log2.loga
import dev.castive.log2.loge
import dev.castive.log2.logv
import dev.castive.log2.logw
import dev.dcas.jmp.spring.security.model.AuthToken
import dev.dcas.jmp.spring.security.oauth2.OAuth2TokenProvider
import dev.dcas.jmp.spring.security.oauth2.impl.AbstractOAuth2Provider
import dev.dcas.util.extend.ellipsize
import dev.dcas.util.spring.responses.BadRequestResponse
import dev.dcas.util.spring.responses.InternalErrorResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RequestMapping("/oauth2")
@RestController
class OAuth2Control @Autowired constructor(
    private val oauth2TokenProvider: OAuth2TokenProvider
) {

    /**
     * Check whether a provider exists
     * Used by the front end for showing social login buttons
     */
    @GetMapping("/api/{name}")
    fun getProvider(@PathVariable("name") name: String): String = oauth2TokenProvider.findProviderByNameOrThrow(name).name

    @GetMapping("/callback")
    fun createToken(@RequestParam("code", required = true) code: String, @RequestParam("state", required = true) state: String): AuthToken {
        val (name, _, _) = AbstractOAuth2Provider.parseState(state)
        "Got callback request for provider: $name".logv(javaClass)
        // get the provider
        val provider = oauth2TokenProvider.findProviderByNameOrThrow(name)
        // create our token
        val token = kotlin.runCatching {
            provider.getAccessToken(code)
        }.onFailure {
            "Failed to extract access token from callback data".loge(javaClass, it)
            throw BadRequestResponse("Unable to extract access token")
        }.getOrThrow()
        // attempt to create the user
        if(oauth2TokenProvider.createUser(token.accessToken, token.refreshToken ?: token.accessToken, provider))
            return AuthToken(token.accessToken, token.refreshToken ?: token.accessToken, "oauth2/${provider.name}")
        else {
            "Failed to create user from token: ${token.accessToken.ellipsize(24)}".loge(javaClass)
            throw InternalErrorResponse()
        }
    }

    /**
     * Redirect the user to an oauth2 provider consent screen
     * No handling is done here, that is done by the callback
     * Note: the user will hit this endpoint directly
     */
    @GetMapping("/{name}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getProviderUrl(@PathVariable("name") name: String): Pair<String, String> {
        val provider = oauth2TokenProvider.findProviderByNameOrThrow(name)
        return provider.name to provider.getAuthoriseUrl()
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/logout/{name}")
    fun revokeToken(@PathVariable("name") name: String, @RequestParam("accessToken", required = true) accessToken: String) {
        // name is given by the ui with the oauth2/ prefix, so we need to strip that
        val providerName = name.removePrefix("oauth2/")
        val provider = oauth2TokenProvider.findProviderByNameOrThrow(providerName)
        "Performing logout for OAuth2 token: ${accessToken.ellipsize(24)}".loga(javaClass)
        /* sometimes revoking the token is unsupported and throws an exception
           this is okay and we still want to return the 200
         */
        kotlin.runCatching {
            provider.revokeTokenAsync(accessToken)
        }.onFailure {
            "Failed to perform logout using provider: $name, it is probably unsupported".logw(javaClass)
        }
    }
}