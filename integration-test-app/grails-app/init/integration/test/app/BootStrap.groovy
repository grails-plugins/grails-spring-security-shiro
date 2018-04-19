package integration.test.app

class BootStrap {

	def init = {

		def save = { it.save(failOnError: true) }

		User user1 = save new User('user1')
		User user2 = save new User('user2')
		User user3 = save new User('user3')
		Role roleAdmin = save new Role('ROLE_ADMIN')
		Role roleUser = save new Role('ROLE_USER')

		save new Permission(user1, 'printer:print:*')

		save new Permission(user2, 'printer:print:*')
		save new Permission(user2, 'printer:maintain:epsoncolor')
		save new Permission(user2, 'action:kick')

		save new Permission(user3, 'action:jump')
		save new Permission(user3, 'action:kick')

		UserRole.create user1, roleAdmin, true
		UserRole.create user2, roleAdmin, true
		UserRole.create user2, roleUser, true

		assert 2 == Role.count()
		assert 3 == User.count()
		assert 3 == UserRole.count()
		assert 6 == Permission.count()
	}
}
