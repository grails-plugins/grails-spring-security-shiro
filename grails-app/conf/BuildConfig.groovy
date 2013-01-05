grails.project.work.dir = 'target'
grails.project.target.level = 1.6

grails.project.dependency.resolution = {

	inherits 'global'
	log 'warn'

	repositories {
		grailsCentral()
		mavenLocal()
		mavenCentral()
	}

	dependencies {

		String shiroVersion = '1.2.1'
		def common = ['easymock', 'groovy-all', 'jcl-over-slf4j', 'junit', 'log4j', 'slf4j-log4j12']

		compile "org.apache.shiro:shiro-aspectj:$shiroVersion", {
			excludes((common + ['aspectjrt', 'aspectjweaver']) as Object[])
		}

		compile "org.apache.shiro:shiro-core:$shiroVersion", {
			excludes((common + ['commons-beanutils', 'hsqldb', 'slf4j-api']) as Object[])
		}

		compile "org.apache.shiro:shiro-spring:$shiroVersion", {
			excludes((common + ['servlet-api', 'spring-context', 'spring-test']) as Object[])
		}

		compile "org.apache.shiro:shiro-web:$shiroVersion", {
			excludes((common + ['jsp-api', 'jstl', 'servlet-api']) as Object[])
		}
	}

	plugins {
		compile ':spring-security-core:1.2.7.3'

		compile(":hibernate:$grailsVersion") {
			export = false
		}

		build(':release:2.1.0', ':rest-client-builder:1.0.3') {
			export = false
		}
	}
}
