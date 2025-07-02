package org.example

import org.example.model.Users

class UserTable {
	private var users: MutableList<Users> = mutableListOf()
	private object Index {
		var innerIndex: Int = 0
	}

	fun addUser(user: Users) {
		Index.innerIndex++

		users.add(user)
		println(users)
	}

	fun getUser(id: String): Users? {
		return users.firstOrNull { it.id == id }
	}

	fun removeUser(id: String) {
		users.remove(users.first { it.id == id })
	}
}