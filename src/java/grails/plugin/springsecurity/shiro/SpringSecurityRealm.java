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
package grails.plugin.springsecurity.shiro;

import java.util.HashSet;
import java.util.Set;

import org.apache.shiro.authc.AccountException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author <a href='mailto:burt@burtbeckwith.com'>Burt Beckwith</a>
 */
public class SpringSecurityRealm extends AuthorizingRealm {

   protected final Logger log = LoggerFactory.getLogger(getClass());

	protected AuthenticationTrustResolver authenticationTrustResolver;
	protected ShiroPermissionResolver permissionResolver;

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

		if (principals == null) {
			throw new AuthorizationException("PrincipalCollection method argument cannot be null.");
		}

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		String username = (String)getAvailablePrincipal(principals);
		/*User user =*/ getCurrentUser(username, authentication);

		Set<String> roleNames = new HashSet<String>();
		for (GrantedAuthority auth : authentication.getAuthorities()) {
			roleNames.add(auth.getAuthority());
		}

		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo(roleNames);
		info.setStringPermissions(permissionResolver.resolvePermissions(username));

		if (log.isDebugEnabled()) {
			log.debug("AuthorizationInfo for user {}: {}", username, DefaultGroovyMethods.dump(info));
		}

		return info;
	}

	protected UserDetails getCurrentUser(String username, Authentication authentication) {
		if (authentication == null || authenticationTrustResolver.isAnonymous(authentication)) {
			throw new AccountException("Not logged in or anonymous");
		}

		UserDetails user = (UserDetails)authentication.getPrincipal();
		if (!user.getUsername().equals(username)) {
			throw new AccountException("Not logged in as expected user");
		}

		return user;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		UsernamePasswordToken upToken = (UsernamePasswordToken)token;
		String username = upToken.getUsername();
		if (username == null) {
			throw new AccountException("Null usernames are not allowed by this realm.");
		}

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDetails user = getCurrentUser(username, authentication);
		return new SimpleAuthenticationInfo(username, user.getPassword().toCharArray(), getName());
	}

	/**
	 * Dependency injection for the AuthenticationTrustResolver.
	 * @param resolver the resolver
	 */
	public void setAuthenticationTrustResolver(AuthenticationTrustResolver resolver) {
		authenticationTrustResolver = resolver;
	}

	/**
	 * Dependency injection for the ShiroPermissionResolver.
	 * @param resolver the resolver
	 */
	public void setShiroPermissionResolver(ShiroPermissionResolver resolver) {
		permissionResolver = resolver;
	}
}
