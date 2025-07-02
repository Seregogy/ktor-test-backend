package org.example.model

import org.jetbrains.exposed.dao.id.IntIdTable

object Users: IntIdTable("users") {
	var name = varchar("name", 32)
	var email = varchar("email", 256).nullable()
	var about = varchar("about", 512).nullable()
	var avatarLink = text("avatarLink").nullable()
}

//@Serializable
//data class User(
//	var id: String,
//	var name: String,
//	var email: String,
//	var passwordHash: String
//)