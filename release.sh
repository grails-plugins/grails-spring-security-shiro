rm -rf target/release
mkdir target/release
cd target/release
git clone git@github.com:grails-plugins/grails-spring-security-shiro.git
cd grails-spring-security-shiro
grails clean
grails compile

#grails publish-plugin --snapshot --stacktrace
grails publish-plugin --stacktrace
