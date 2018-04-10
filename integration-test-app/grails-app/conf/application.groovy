import integration.test.app.Permission
import integration.test.app.Role
import integration.test.app.User
import integration.test.app.UserRole

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
