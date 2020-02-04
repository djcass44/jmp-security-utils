/*
 *   This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.dcas.jmp.spring.security.service

import dev.castive.log2.*
import dev.dcas.jmp.spring.security.BasicAuthProvider
import dev.dcas.jmp.spring.security.SecurityConstants
import dev.dcas.jmp.spring.security.model.BasicAuth
import dev.dcas.jmp.spring.security.model.UserProjection
import dev.dcas.jmp.spring.security.model.entity.UserEntity
import dev.dcas.jmp.spring.security.model.repo.UserRepository
import dev.dcas.util.spring.responses.ConflictResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.ldap.core.AttributesMapper
import org.springframework.ldap.core.DirContextOperations
import org.springframework.ldap.core.LdapTemplate
import org.springframework.ldap.core.support.AbstractContextMapper
import org.springframework.ldap.filter.EqualsFilter
import org.springframework.ldap.query.LdapQueryBuilder
import org.springframework.ldap.query.SearchScope
import org.springframework.ldap.support.LdapUtils
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct
import javax.naming.directory.Attributes


@Service
class LdapService @Autowired constructor(
    private val userRepo: UserRepository,
    private val ldapTemplate: LdapTemplate
): BasicAuthProvider {

    @Value("\${spring.ldap.enabled:false}")
    val enabled: Boolean = false

    override val sourceName: String
        get() = SecurityConstants.sourceLdap


    @PostConstruct
    fun init() {
        "LDAP Jwt generation enabled: $enabled".loga(javaClass)
    }

    /**
     * Extracts an LDAP distinguished name from a uid
     * https://docs.spring.io/spring-ldap/docs/1.3.x/reference/html/user-authentication.html
     */
    private fun getDnForUser(uid: String): String? {
        val f = EqualsFilter("uid", uid)
        val result = ldapTemplate.search(LdapUtils.emptyLdapName(), f.toString(),
            object : AbstractContextMapper<Any>() {
                override fun doMapFromContext(ctx: DirContextOperations): Any {
                    return ctx.nameInNamespace
                }
            })
        if (result.size != 1) {
            "Failed to find user with uid=$uid".logi(javaClass)
            return null
        }
        return result[0] as String
    }

    /**
     * Search the directory for a user with a matching DN
     * Only the first item is returned
     */
    private fun getUser(uid: String): UserEntity? {
        "Attempting to extract user details from LDAP where uid=$uid".logv(javaClass)
        val query = LdapQueryBuilder.query()
            .searchScope(SearchScope.SUBTREE)
            .timeLimit(3_000)
            .countLimit(1)
            .base(LdapUtils.emptyLdapName())
            .where("uid").`is`(uid)
        return kotlin.runCatching {
            ldapTemplate.search(query, UserAttributeMapper(userRepo)).first()
        }.onFailure {
            "Failed to execute LDAPService::getUser for uid=$uid".loge(javaClass, it)
        }.getOrNull()
    }

    override fun getUserByName(basicAuth: BasicAuth): UserEntity? {
        if(!enabled)
            return null
        val (username, password) = basicAuth
        // get the users DN from their username
        val dn = getDnForUser(username) ?: return null
        val ctx = kotlin.runCatching {
            // check that we are able to bind using the users credentials
            ldapTemplate.contextSource.getContext(dn, password)
        }.onFailure {
            "Failed to bind LDAP using DN: $dn".loge(javaClass, it)
        }.getOrNull()
        // context must be close
        if(ctx == null)
            return null
        else
            LdapUtils.closeContext(ctx)
        "Successfully created LDAP binding to $dn".logd(javaClass)
        return getUser(username)
    }

    /**
     * Converts LDAP attributes into a JMP user
     */
    private class UserAttributeMapper(
        private val userRepo: UserRepository
    ): AttributesMapper<UserEntity> {
        override fun mapFromAttributes(attr: Attributes): UserEntity {
            val username = "ldap/${attr.get("uid").get() as String}"
            // if there is an existing user, return them
            userRepo.findFirstByUsernameAndSource(username, SecurityConstants.sourceLdap)?.let {
                "Found existing match for LDAP user: $username".logv(javaClass)
                return it
            }
            // we cannot have duplicate usernames
            if(userRepo.existsByUsername(username)) {
                "Blocking possible merger of new ldap user and existing user: $username".logw(javaClass)
                throw ConflictResponse("Username is already in use")
            }
            // otherwise, create a new user
            "Creating new user representation for LDAP user: $username".logi(javaClass)
            return userRepo.createWithData(SecurityConstants.sourceLdap, username, UserProjection(username, attr.get("cn").get() as String, null, SecurityConstants.sourceLdap))
        }
    }
}