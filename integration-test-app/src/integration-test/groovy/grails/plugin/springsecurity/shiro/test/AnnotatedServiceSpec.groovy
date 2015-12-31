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
import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import org.apache.shiro.SecurityUtils
import org.apache.shiro.authz.UnauthenticatedException
import org.apache.shiro.authz.UnauthorizedException
import org.apache.shiro.realm.Realm
import org.apache.shiro.util.ThreadContext
import org.apache.shiro.web.mgt.WebSecurityManager
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification

/**
 * @author <a href='mailto:burt@burtbeckwith.com'>Burt Beckwith</a>
 */
@Integration
@Rollback
@Transactional
class AnnotatedServiceSpec extends Specification {

	WebSecurityManager shiroSecurityManager
	Realm springSecurityRealm
	def testService

	private request = new MockHttpServletRequest()
	private response = new MockHttpServletResponse()

	void setup() {
		assert shiroSecurityManager
		ThreadContext.bind shiroSecurityManager

		logout()
	}

	void testOnePermission() {

		when:
		testService.adminPrinter()

		then:
		thrown UnauthenticatedException

		when:
		login 'user1'
		testService.adminPrinter()

		then:
		thrown UnauthorizedException

		when:
		logout()
		login 'user2'

		then:
		testService.adminPrinter()

		when:
		logout()
		testService.adminPrinter()

		then:
		thrown UnauthenticatedException
	}

	void testRequireTwoPermissions() {
		when:
		testService.requireJumpAndKick()

		then:
		thrown UnauthenticatedException

		when:
		login 'user2'
		testService.requireJumpAndKick()

		then:
		thrown UnauthorizedException

		when:
		logout()
		login 'user3'

		then:
		testService.requireJumpAndKick()

		when:
		logout()
		testService.requireJumpAndKick()

		then:
		thrown UnauthenticatedException
	}

	void testRequireOneOrTwoPermissions() {

		when:
		testService.requireJumpOrKick()

		then:
		thrown UnauthenticatedException

		when:
		login 'user1'
		testService.requireJumpOrKick()

		then:
		thrown UnauthorizedException

		when:
		logout()
		login 'user2'

		then:
		testService.requireJumpOrKick()

		when:
		logout()
		login 'user3'

		then:
		testService.requireJumpOrKick()

		when:
		logout()
		testService.requireJumpOrKick()

		then:
		thrown UnauthenticatedException
	}

	void testNonexistentPermissions() {

		when:
		testService.impossiblePermissions()

		then:
		thrown UnauthenticatedException

		when:
		login 'user1'
		testService.impossiblePermissions()

		then:
		thrown UnauthorizedException
	}

	void testRequiresUser() {
		when:
		testService.requireUser()

		then:
		thrown UnauthenticatedException

		when:
		login 'user1'

		then:
		testService.requireUser()
	}

	void testRequireOneOrTwoRoles() {

		when:
		testService.requireUserOrAdmin()

		then:
		thrown UnauthenticatedException

		when:
		login 'user1'

		then:
		testService.requireUserOrAdmin()

		when:
		logout()
		login 'user2'

		then:
		testService.requireUserOrAdmin()

		when:
		logout()
		login 'user3'
		testService.requireUserOrAdmin()

		then:
		thrown UnauthorizedException
		logout()

		when:
		testService.requireUserOrAdmin()

		then:
		thrown UnauthenticatedException
	}

	void testRequireTwoRoles() {

		when:
		testService.requireUserAndAdmin()

		then:
		thrown UnauthenticatedException

		when:
		login 'user1'
		testService.requireUserAndAdmin()

		then:
		thrown UnauthorizedException

		when:
		logout()
		login 'user2'

		then:
		testService.requireUserAndAdmin()

		when:
		logout()
		login 'user3'
		testService.requireUserAndAdmin()

		then:
		thrown UnauthorizedException

		when:
		logout()
		testService.requireUserAndAdmin()

		then:
		thrown UnauthenticatedException
	}

	void testRequiresGuest() {
		expect:
		testService.requireGuest()

		when:
		login 'user1'
		testService.requireGuest()

		then:
		UnauthenticatedException e = thrown()
		e.message.startsWith 'Attempting to perform a guest-only operation'
	}

	void testRequiresAuthentication() {
		when:
		testService.requireAuthentication()

		then:
		thrown UnauthenticatedException

		when:
		login 'user1'

		then:
		testService.requireAuthentication()
	}

	private void login(String username) {
		SpringSecurityUtils.reauthenticate username, 'password'
		request.getSession()
		ShiroUtils.bindSubject SecurityContextHolder.context.authentication,
			springSecurityRealm, shiroSecurityManager, request, response
	}

	private void logout() {
		SecurityContextHolder.clearContext()
		SecurityUtils.subject.logout()
	}
}
