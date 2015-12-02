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

import grails.plugin.springsecurity.web.SecurityRequestHolder;

import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.web.authentication.switchuser.AuthenticationSwitchUserEvent;

/**
 * @author <a href='mailto:burt@burtbeckwith.com'>Burt Beckwith</a>
 */
public class ShiroSpringSecurityEventListener implements ApplicationListener<AbstractAuthenticationEvent> {

   protected final Logger log = LoggerFactory.getLogger(getClass());

	protected SecurityManager securityManager;
	protected Realm realm;

	public void onApplicationEvent(AbstractAuthenticationEvent event) {

		log(event);

		if (event instanceof AuthenticationSuccessEvent || event instanceof InteractiveAuthenticationSuccessEvent) {
			ShiroUtils.bindSubject(event.getAuthentication(), realm, securityManager,
					SecurityRequestHolder.getRequest(), SecurityRequestHolder.getResponse());
		}
		else if (event instanceof AuthenticationSwitchUserEvent) {
			// TODO
		}
	}

	protected void log(AbstractAuthenticationEvent event) {
		log.debug("on{} for Authentication {}", event.getAuthentication(), event.getClass().getSimpleName());
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
