/*
 *   This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.dcas.jmp.security.shim

import dev.dcas.jmp.security.shim.impl.GroupRepository
import dev.dcas.jmp.security.shim.impl.SessionRepository
import dev.dcas.jmp.security.shim.impl.UserRepository
import dev.dcas.jmp.security.shim.repo.SessionRepoCustomImpl
import org.springframework.boot.autoconfigure.AutoConfigurationPackage
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Import
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Import(
	UserRepository::class,
	SessionRepository::class,
	GroupRepository::class,
	SessionRepoCustomImpl::class
)
@EntityScan(basePackages = ["dev.dcas.jmp.security.shim", "dev.castive.jmp"])
@EnableJpaRepositories(basePackages = ["dev.dcas.jmp.security.shim", "dev.castive.jmp"])
@AutoConfigurationPackage
class SecurityDataShimAutoConfiguration