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

import grails.core.GrailsApplication
import grails.plugin.springsecurity.SpringSecurityUtils
import org.apache.shiro.authz.Permission
import org.apache.shiro.authz.permission.RolePermissionResolver
import org.apache.shiro.authz.permission.WildcardPermission

class GormShiroRolePermissionResolver implements RolePermissionResolver {

	GrailsApplication grailsApplication

	@Override
	Collection<Permission> resolvePermissionsInRole(String roleString) {
		ConfigObject conf = SpringSecurityUtils.securityConfig

		String rolePermissionDomainClassName = conf.shiro.rolePermissionDomainClassName
		if (!rolePermissionDomainClassName) {
			throw new RuntimeException('No value specified for the Shiro role permission class; ' +
					'set the grails.plugin.springsecurity.shiro.rolePermissionDomainClassName attribute')
		}

		def rpdc = grailsApplication.getDomainClass(rolePermissionDomainClassName)
		if (!rpdc) {
			throw new RuntimeException("The specified role permission domain class '$rolePermissionDomainClassName' is not a domain class")
		}

		Class<?> RolePermission = rpdc.clazz

		// Role property name comes from Spring Security
		def rolePropertyName = conf.authority.nameField

		if (!rolePropertyName) {
			throw new RuntimeException("The Spring Security authority.nameField is not defined.")
		}

		List<String> stringPermissions = RolePermission.withCriteria {
			role {
				eq rolePropertyName, roleString
			}
			projections {
				property 'permission'
			}
		}

		List<Permission> permissions = []

		stringPermissions.each { String perm ->
			permissions << new WildcardPermission(perm)
		}

		return permissions
	}
}
