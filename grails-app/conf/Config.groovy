import grails.plugin.springsecurity.shiro.test.Permission
import grails.plugin.springsecurity.shiro.test.Role
import grails.plugin.springsecurity.shiro.test.User
import grails.plugin.springsecurity.shiro.test.UserRole

log4j = {
	error 'org.codehaus.groovy.grails',
	      'org.springframework',
	      'org.hibernate',
	      'net.sf.ehcache.hibernate'
	debug 'grails.plugin.springsecurity',
	      'grails.plugins.springsecurity',
	      'org.codehaus.groovy.grails.plugins.springsecurity'
}

grails.plugins.springsecurity.userLookup.userDomainClassName = User.name
grails.plugins.springsecurity.userLookup.authorityJoinClassName = UserRole.name
grails.plugins.springsecurity.authority.className = Role.name
grails.plugins.springsecurity.shiro.permissionDomainClassName = Permission.name
