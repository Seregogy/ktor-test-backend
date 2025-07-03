package org.example.model

import org.jetbrains.exposed.dao.id.IntIdTable

object Users: IntIdTable("USERS") {
	var name = varchar("name", 32)
	var email = varchar("email", 256).nullable()
	var about = varchar("about", 512).nullable()
	var avatarLink = text("avatarLink").nullable()
	var passwordHash = text("passwordHash").nullable()
}