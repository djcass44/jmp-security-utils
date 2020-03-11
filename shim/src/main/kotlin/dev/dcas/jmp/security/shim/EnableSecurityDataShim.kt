/*
 *   This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.dcas.jmp.security.shim

import dev.dcas.jmp.security.shim.config.ShimConfigRef
import dev.dcas.jmp.security.shim.impl.GroupRepository
import dev.dcas.jmp.security.shim.impl.SessionRepository
import dev.dcas.jmp.security.shim.impl.UserRepository
import dev.dcas.jmp.security.shim.repo.SessionRepoCustomImpl
import org.springframework.context.annotation.Import

@Deprecated("Replaced by AutoConfiguration")
@Import(
	ShimConfigRef::class,
	GroupRepository::class,
	SessionRepository::class,
	UserRepository::class,
	SessionRepoCustomImpl::class
)
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class EnableSecurityDataShim