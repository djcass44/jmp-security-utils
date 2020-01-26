package dev.dcas.jmp.spring.security.config

import org.springframework.security.config.annotation.web.builders.HttpSecurity

interface SecurityConfigurerAdapter {
	fun configure(http: HttpSecurity)
}