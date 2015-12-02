grails.project.work.dir = 'target'
grails.project.docs.output.dir = 'docs/manual' // for backwards-compatibility, the docs are checked into gh-pages branch

grails.project.dependency.resolver = 'maven'
grails.project.dependency.resolution = {

	inherits 'global'
	log 'warn'

	repositories {
		grailsCentral()
		mavenLocal()
		mavenCentral()
	}

	dependencies {

		String shiroVersion = '1.2.4'
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
		compile ':spring-security-core:2.0-RC6'

		compile ':hibernate:3.6.10.18', {
			export = false
		}

		build ':release:3.1.2', ':rest-client-builder:2.1.1', {
			export = false
		}
	}
}
