/*
 *   This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.dcas.jmp.spring.security.config

import dev.castive.log2.loga
import dev.dcas.jmp.spring.security.props.SecurityProps
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
class WebSecurityConfig @Autowired constructor(
    private val securityProps: SecurityProps
): WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        // disable CSRF
        http.csrf().disable()

        // allow cors if enabled
        if(securityProps.allowCors) {
            "Enabling CORS requests for REST resources".loga(javaClass)
            http.cors().configurationSource(corsConfigurationSource())
        }

        // disable sessions
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

        // entrypoints
        http.authorizeRequests().anyRequest().permitAll()
    }

    override fun configure(web: WebSecurity) {
        // allow swagger access without authentication
        web.ignoring().antMatchers(
            "/v2/api-docs",
            "/swagger-resources/**",
            "/swagger-ui.html",
            "/configuration/**",
            "/webjars/**",
            "/public"
        )
    }

    @ConditionalOnMissingBean(CorsConfigurationSource::class)
    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        return UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", CorsConfiguration().applyPermitDefaultValues().apply {
                addAllowedMethod(HttpMethod.DELETE)
                addAllowedMethod(HttpMethod.PATCH)
                addAllowedMethod(HttpMethod.OPTIONS)
                addAllowedMethod(HttpMethod.PUT)
            })
        }
    }
}