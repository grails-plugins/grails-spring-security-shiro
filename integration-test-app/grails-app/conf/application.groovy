import grails.plugin.springsecurity.shiro.test.Permission
import grails.plugin.springsecurity.shiro.test.Role
import grails.plugin.springsecurity.shiro.test.User
import grails.plugin.springsecurity.shiro.test.UserRole

grails {
	plugin {
		springsecurity {
			userLookup {
				userDomainClassName = User.name
				authorityJoinClassName = UserRole.name
			}
			authority.className = Role.name
			shiro.permissionDomainClassName = Permission.name
		}
	}
}
