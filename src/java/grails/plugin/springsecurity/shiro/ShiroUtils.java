/* Copyright 2013 SpringSource.
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.support.SubjectThreadState;
import org.apache.shiro.web.session.HttpServletSession;
import org.apache.shiro.web.subject.WebSubjectContext;
import org.apache.shiro.web.subject.support.DefaultWebSubjectContext;
import org.apache.shiro.web.subject.support.WebDelegatingSubject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;

/**
 * @author <a href='mailto:burt@burtbeckwith.com'>Burt Beckwith</a>
 */
public class ShiroUtils {

	protected static Logger log = LoggerFactory.getLogger(ShiroUtils.class);

	protected static final char[] PASSWORD_CHARS = "password".toCharArray();

	protected ShiroUtils() {
		// static only
	}

	public static void bindSubject(Authentication authentication, Realm realm, SecurityManager securityManager,
			HttpServletRequest request, HttpServletResponse response) {

		User user = (User)authentication.getPrincipal();

		boolean isSessionCreationEnabled = false; // TODO
		HttpSession session = request.getSession(isSessionCreationEnabled);
		String host = request.getRemoteHost();

		String username = user.getUsername();
		String realmName = realm.getName();

		AuthenticationToken token = new UsernamePasswordToken(username, PASSWORD_CHARS, false, host);
		SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(username, PASSWORD_CHARS, realmName);
		PrincipalCollection principals = new SimplePrincipalCollection(username, realmName);
		info.setPrincipals(principals);

		WebSubjectContext context = new DefaultWebSubjectContext();
		context.setSecurityManager(securityManager);
		context.setPrincipals(principals);
		context.setAuthenticationToken(token);
		context.setAuthenticationInfo(info);
		context.setHost(host);

		WebDelegatingSubject subject = new WebDelegatingSubject(
				principals, true, host, new HttpServletSession(session, host),
				isSessionCreationEnabled, request, response, securityManager);

		if (log.isDebugEnabled()) {
			log.debug("Binding subject for principal {} from host {}", username, host);
		}

		new SubjectThreadState(subject).bind();
	}
}
