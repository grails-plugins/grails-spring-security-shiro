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
package integration.test.app

import org.apache.commons.lang.builder.HashCodeBuilder

/**
 * @author <a href='mailto:burt@burtbeckwith.com'>Burt Beckwith</a>
 */
class UserRole implements Serializable {

	User user
	Role role

	boolean equals(other) {
		if (!(other instanceof UserRole)) {
			return false
		}

		other.user?.id == user?.id &&
			other.role?.id == role?.id
	}

	int hashCode() {
		def builder = new HashCodeBuilder()
		if (user) builder.append(user.id)
		if (role) builder.append(role.id)
		builder.toHashCode()
	}

	static UserRole get(long userId, long roleId) {
		find 'from UserRole where user.id=:userId and role.id=:roleId',
			[userId: userId, roleId: roleId]
	}

	static UserRole create(User user, Role role, boolean flush = false) {
		new UserRole(user: user, role: role).save(flush: flush)
	}

	static boolean remove(User user, Role role, boolean flush = false) {
		UserRole instance = UserRole.findByUserAndRole(user, role)
		if (!instance) {
			return false
		}

		instance.delete(flush: flush)
		true
	}

	static void removeAll(User user) {
		executeUpdate 'DELETE FROM UserRole WHERE user=:user', [user: user]
	}

	static void removeAll(Role role) {
		executeUpdate 'DELETE FROM UserRole WHERE role=:role', [role: role]
	}

	static mapping = {
		id composite: ['role', 'user']
		version false
	}
}
