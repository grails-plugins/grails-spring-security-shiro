import test.Permission
import test.Role
import test.User
import test.UserRole

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
