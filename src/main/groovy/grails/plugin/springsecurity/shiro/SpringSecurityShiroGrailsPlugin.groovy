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
package grails.plugin.springsecurity.shiro

import grails.plugin.springsecurity.SecurityFilterPosition
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugins.Plugin
import groovy.util.logging.Slf4j
import org.apache.shiro.cache.MemoryConstrainedCacheManager
import org.apache.shiro.spring.LifecycleBeanPostProcessor
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor
import org.apache.shiro.web.mgt.DefaultWebSecurityManager
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator

/**
 * @author <a href='mailto:burt@burtbeckwith.com'>Burt Beckwith</a>
 */
@Slf4j
class SpringSecurityShiroGrailsPlugin extends Plugin {

	String grailsVersion = '3.0.0 > *'
	String author = 'Burt Beckwith'
	String authorEmail = 'burt@burtbeckwith.com'
	String title = 'Shiro support for the Spring Security plugin'
	String description = 'Shiro support for the Spring Security plugin'
	String documentation = 'http://grails-plugins.github.io/grails-spring-security-shiro/'
	String license = 'APACHE'
	def organization = [name: 'grails', url: 'http://www.grails.org/']
	def issueManagement = [url: 'https://github.com/grails-plugins/grails-spring-security-shiro/issues']
	def scm = [url: 'https://github.com/grails-plugins/grails-spring-security-shiro']
	def loadAfter = ['springSecurityCore']
	def profiles = ['web']

	Closure doWithSpring() {{ ->

		def conf = SpringSecurityUtils.securityConfig
		if (!conf || !conf.active) {
			return
		}

		SpringSecurityUtils.loadSecondaryConfig 'DefaultShiroSpringSecurityConfig'
		// have to get again after overlaying DefaultShiroSpringSecurityConfig
		conf = SpringSecurityUtils.securityConfig

		if (!conf.shiro.active) {
			return
		}

		boolean printStatusMessages = (conf.printStatusMessages instanceof Boolean) ? conf.printStatusMessages : true

		if (printStatusMessages) {
			println '\nConfiguring Spring Security Shiro ...'
		}

		SpringSecurityUtils.registerFilter 'shiroSubjectBindingFilter',
				SecurityFilterPosition.SECURITY_CONTEXT_FILTER.order + 1

		shiroLifecycleBeanPostProcessor(LifecycleBeanPostProcessor)

		shiroAdvisorAutoProxyCreator(DefaultAdvisorAutoProxyCreator) { bean ->
			bean.dependsOn = 'shiroLifecycleBeanPostProcessor'
			proxyTargetClass = true
		}

		shiroAttributeSourceAdvisor(AuthorizationAttributeSourceAdvisor) {
			securityManager = ref('shiroSecurityManager')
		}

		shiroPermissionResolver(GormShiroPermissionResolver) {
			grailsApplication = grailsApplication
		}

		boolean useCache = conf.shiro.useCache // true

		springSecurityRealm(SpringSecurityRealm) {
			authenticationTrustResolver = ref('authenticationTrustResolver')
			shiroPermissionResolver = ref('shiroPermissionResolver')
			if (useCache) {
				cacheManager = ref('shiroCacheManager')
			}
		}

		if (useCache) {
			shiroCacheManager(MemoryConstrainedCacheManager)
		}

		shiroSecurityManager(DefaultWebSecurityManager) { bean ->
			realm = ref('springSecurityRealm')
			if (useCache) {
				cacheManager = ref('shiroCacheManager')
			}
		}

		shiroSpringSecurityEventListener(ShiroSpringSecurityEventListener) {
			realm = ref('springSecurityRealm')
			securityManager = ref('shiroSecurityManager')
		}

		shiroSubjectBindingFilter(ShiroSubjectBindingFilter) {
			authenticationTrustResolver = ref('authenticationTrustResolver')
			realm = ref('springSecurityRealm')
			securityManager = ref('shiroSecurityManager')
		}

		shiroLogoutHandler(ShiroLogoutHandler)

		if (printStatusMessages) {
			println '... finished configuring Spring Security Shiro\n'
		}
	}}

	void doWithApplicationContext() {
		def conf = SpringSecurityUtils.securityConfig
		if (!conf || !conf.active || !conf.shiro.active) {
			return
		}

		applicationContext.logoutHandlers.add 0, applicationContext.shiroLogoutHandler // must be before SecurityContextLogoutHandler
	}
}
