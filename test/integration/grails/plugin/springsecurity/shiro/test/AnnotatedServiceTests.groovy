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
package grails.plugin.springsecurity.shiro.test

import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugin.springsecurity.shiro.ShiroUtils

import org.apache.shiro.SecurityUtils
import org.apache.shiro.authz.UnauthenticatedException
import org.apache.shiro.authz.UnauthorizedException
import org.apache.shiro.util.ThreadContext
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder

/**
 * @author <a href='mailto:burt@burtbeckwith.com'>Burt Beckwith</a>
 */
class AnnotatedServiceTests extends GroovyTestCase {

	def shiroSecurityManager
	def springSecurityRealm
	def testService

	private request = new MockHttpServletRequest()
	private response = new MockHttpServletResponse()

	protected void setUp() {
		super.setUp()

		User user1 = new User(username: 'user1', password: 'password', enabled: true).save(failOnError: true)
		User user2 = new User(username: 'user2', password: 'password', enabled: true).save(failOnError: true)
		User user3 = new User(username: 'user3', password: 'password', enabled: true).save(failOnError: true)
		Role roleAdmin = new Role(authority: 'ROLE_ADMIN').save(failOnError: true)
		Role roleUser = new Role(authority: 'ROLE_USER').save(failOnError: true)

		new Permission(user: user1, permission: 'printer:print:*').save(failOnError: true)

		new Permission(user: user2, permission: 'printer:print:*').save(failOnError: true)
		new Permission(user: user2, permission: 'printer:maintain:epsoncolor').save(failOnError: true)
		new Permission(user: user2, permission: 'action:kick').save(failOnError: true)

		new Permission(user: user3, permission: 'action:jump').save(failOnError: true)
		new Permission(user: user3, permission: 'action:kick').save(failOnError: true)

		UserRole.create user1, roleAdmin, true
		UserRole.create user2, roleAdmin, true
		UserRole.create user2, roleUser, true

		assert 2 == Role.count()
		assert 3 == User.count()
		assert 3 == UserRole.count()
		assert 6 == Permission.count()

		ThreadContext.bind shiroSecurityManager
	}

	void testOnePermission() {

		shouldFail(UnauthenticatedException) {
			testService.adminPrinter()
		}

		login 'user1'
		shouldFail(UnauthorizedException) {
			testService.adminPrinter()
		}
		logout()

		login 'user2'
		assert testService.adminPrinter()
		logout()

		shouldFail(UnauthenticatedException) {
			testService.adminPrinter()
		}
	}

	void testRequireTwoPermissions() {
		shouldFail(UnauthenticatedException) {
			testService.requireJumpAndKick()
		}

		login 'user2'
		shouldFail(UnauthorizedException) {
			testService.requireJumpAndKick()
		}
		logout()

		login 'user3'
		assert testService.requireJumpAndKick()
		logout()

		shouldFail(UnauthenticatedException) {
			testService.requireJumpAndKick()
		}
	}

	void testRequireOneOrTwoPermissions() {

		shouldFail(UnauthenticatedException) {
			testService.requireJumpOrKick()
		}

		login 'user1'
		shouldFail(UnauthorizedException) {
			testService.requireJumpOrKick()
		}
		logout()

		login 'user2'
		assert testService.requireJumpOrKick()
		logout()

		login 'user3'
		assert testService.requireJumpOrKick()
		logout()

		shouldFail(UnauthenticatedException) {
			testService.requireJumpOrKick()
		}
	}

	void testNonexistentPermissions() {

		shouldFail(UnauthenticatedException) {
			testService.impossiblePermissions()
		}

		login 'user1'
		shouldFail(UnauthorizedException) {
			testService.impossiblePermissions()
		}
	}

	void testRequiresUser() {
		shouldFail(UnauthenticatedException) {
			testService.requireUser()
		}

		login 'user1'
		assert testService.requireUser()
	}

	void testRequireOneOrTwoRoles() {

		shouldFail(UnauthenticatedException) {
			testService.requireUserOrAdmin()
		}

		login 'user1'
		assert testService.requireUserOrAdmin()
		logout()

		login 'user2'
		assert testService.requireUserOrAdmin()
		logout()

		login 'user3'
		shouldFail(UnauthorizedException) {
			testService.requireUserOrAdmin()
		}
		logout()

		shouldFail(UnauthenticatedException) {
			testService.requireUserOrAdmin()
		}
	}

	void testRequireTwoRoles() {

		shouldFail(UnauthenticatedException) {
			testService.requireUserAndAdmin()
		}

		login 'user1'
		shouldFail(UnauthorizedException) {
			testService.requireUserAndAdmin()
		}
		logout()

		login 'user2'
		assert testService.requireUserAndAdmin()
		logout()

		login 'user3'
		shouldFail(UnauthorizedException) {
			testService.requireUserAndAdmin()
		}
		logout()

		shouldFail(UnauthenticatedException) {
			testService.requireUserAndAdmin()
		}
	}

	void testRequiresGuest() {
		assert testService.requireGuest()

		login 'user1'
		String message = shouldFail(UnauthenticatedException) {
			testService.requireGuest()
		}
		assert message.startsWith('Attempting to perform a guest-only operation')
	}

	void testRequiresAuthentication() {
		shouldFail(UnauthenticatedException) {
			testService.requireAuthentication()
		}

		login 'user1'
		assert testService.requireAuthentication()
	}

	@Override
	protected void tearDown() {
		super.tearDown()
		logout()
	}

	private void login(String username) {
		SpringSecurityUtils.reauthenticate username, 'password'
		request.getSession()
		ShiroUtils.bindSubject SecurityContextHolder.context.authentication,
			springSecurityRealm, shiroSecurityManager, request, response
	}

	private void logout() {
		SecurityContextHolder.context.authentication = null
		SecurityUtils.subject.logout()
	}
}
