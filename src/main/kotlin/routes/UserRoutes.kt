package org.example.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.example.model.Users
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class UserDTO(
	val id: Int,
	val name: String,
	val email: String?,
	val about: String?
)

fun Application.userRoutes() {
	routing {
		authenticate("auth-jwt") {
			route("api/v1/users") {
				get("/{id}") {
					val id = call.parameters["id"]?.toInt() ?: return@get call.respond(
						HttpStatusCode.BadRequest,
						mapOf("message" to "Parameter 'id' is not specified"),
					)

					val user = transaction {
						Users.selectAll().where { Users.id eq id }.firstOrNull()
					}

					if (user == null) {
						call.respond(
							HttpStatusCode.NotFound,
							mapOf("message" to "Not Found")
						)
					} else {
						call.respond(
							UserDTO(
								user[Users.id].value,
								user[Users.name],
								user[Users.email],
								user[Users.about]
							)
						)
					}
				}

				post {
					val userDto = call.receive<UserDTO>()

					val userId = transaction {
						Users.insertAndGetId {
							it[Users.name] = userDto.name
							it[Users.email] = userDto.email
							it[Users.about] = userDto.about
						}
					}

					call.respond(
						HttpStatusCode.Created,
						mapOf("id" to userId.value)
					)
				}

				delete("/{id}") {
					val id = call.parameters["id"]?: return@delete call.respond(
						HttpStatusCode.BadRequest,
						mapOf("message" to "Parameter 'id' is not specified"),
					)

					val deleteCount = transaction {
						Users.deleteWhere { Users.id eq id.toInt() }
					}

					if (deleteCount > 0) {
						call.respond(
							HttpStatusCode.NoContent,
							mapOf(
								"message" to "Deleted",
								"id" to id.toInt()
							)
						)
					} else {
						call.respond(
							HttpStatusCode.NotFound,
							mapOf("message" to "Not Found")
						)
					}
				}
			}
		}
	}
}