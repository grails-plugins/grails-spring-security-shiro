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

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.util.ThreadContext;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Fires on every request and configures Shiro auth based on Spring Security auth.
 *
 * @author <a href='mailto:burt@burtbeckwith.com'>Burt Beckwith</a>
 */
public class ShiroSubjectBindingFilter extends OncePerRequestFilter {

	protected AuthenticationTrustResolver authenticationTrustResolver;
	protected Realm realm;
	protected SecurityManager securityManager;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		try {
			bind(request, response);
			chain.doFilter(request, response);
		}
		finally {
			unbind();
		}
	}

	protected void bind(HttpServletRequest request, HttpServletResponse response) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || authenticationTrustResolver.isAnonymous(authentication)) {
			ThreadContext.bind(securityManager);
		}
		else {
			ShiroUtils.bindSubject(authentication, realm, securityManager, request, response);
		}
	}

	protected void unbind() {
		ThreadContext.unbindSubject();
		ThreadContext.unbindSecurityManager();
	}

	/**
	 * Dependency injection for the AuthenticationTrustResolver.
	 * @param resolver the resolver
	 */
	public void setAuthenticationTrustResolver(AuthenticationTrustResolver resolver) {
		authenticationTrustResolver = resolver;
	}

	/**
	 * Dependency injection for the realm.
	 * @param r the realm
	 */
	public void setRealm(Realm r) {
		realm = r;
	}

	/**
	 * Dependency injection for the security manager.
	 * @param manager the manager
	 */
	public void setSecurityManager(SecurityManager manager) {
		securityManager = manager;
	}
}
