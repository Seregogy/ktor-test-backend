package org.example.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.example.model.UsersTable
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class UserDTO(
	val id: String,
	val name: String,
	val email: String?,
	val about: String?
)

fun Application.userRoutes() {
	routing {
		authenticate("auth-jwt") {
			route("api/v1/users") {

				//TODO("User methods")

			}
		}
	}
}