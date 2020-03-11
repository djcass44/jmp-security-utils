/*
 *   This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.dcas.jmp.security.shim

import org.springframework.boot.autoconfigure.AutoConfigurationPackage
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@EntityScan(basePackages = ["dev.dcas.jmp.security.shim", "dev.castive.jmp"])
@EnableJpaRepositories(basePackages = ["dev.dcas.jmp.security.shim", "dev.castive.jmp"])
@AutoConfigurationPackage
class SecurityDataShimAutoConfiguration