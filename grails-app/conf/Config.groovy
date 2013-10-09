import grails.plugin.springsecurity.shiro.test.Permission
import grails.plugin.springsecurity.shiro.test.Role
import grails.plugin.springsecurity.shiro.test.User
import grails.plugin.springsecurity.shiro.test.UserRole

log4j = {
	error 'org.codehaus.groovy.grails',
	      'org.springframework',
	      'org.hibernate',
	      'net.sf.ehcache.hibernate'
	debug 'grails.plugin.springsecurity'
}

grails.plugin.springsecurity.userLookup.userDomainClassName = User.name
grails.plugin.springsecurity.userLookup.authorityJoinClassName = UserRole.name
grails.plugin.springsecurity.authority.className = Role.name
grails.plugin.springsecurity.shiro.permissionDomainClassName = Permission.name
