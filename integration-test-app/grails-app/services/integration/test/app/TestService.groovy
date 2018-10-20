/* Copyright 2013-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package integration.test.app

import org.apache.shiro.authz.annotation.Logical
import org.apache.shiro.authz.annotation.RequiresAuthentication
import org.apache.shiro.authz.annotation.RequiresGuest
import org.apache.shiro.authz.annotation.RequiresPermissions
import org.apache.shiro.authz.annotation.RequiresRoles
import org.apache.shiro.authz.annotation.RequiresUser

/**
 * @author <a href='mailto:burt@burtbeckwith.com'>Burt Beckwith</a>
 */
class TestService {

	@RequiresPermissions('printer:maintain:epsoncolor')
	boolean adminPrinter() { true }

	@RequiresPermissions(['action:jump', 'action:kick'])
	boolean requireJumpAndKick() { true }

	@RequiresPermissions(value=['action:jump', 'action:kick'], logical=Logical.OR)
	boolean requireJumpOrKick() { true }

	@RequiresPermissions('doesnt:exist')
	boolean impossiblePermissions() { true }

	@RequiresUser
	boolean requireUser() { true }

	@RequiresRoles(value=['ROLE_ADMIN', 'ROLE_USER'], logical=Logical.OR)
	boolean requireUserOrAdmin() { true }

	@RequiresRoles(value=['ROLE_ADMIN', 'ROLE_USER'])
	boolean requireUserAndAdmin() { true }

	@RequiresGuest
	boolean requireGuest() { true }

	@RequiresAuthentication
	boolean requireAuthentication() { true }

	@RequiresPermissions('printer:admin')
	boolean requirePrinterAdminPermissions() { true }

	@RequiresPermissions('printer:use')
	boolean requireUsePrinterPermissions() { true }
}
