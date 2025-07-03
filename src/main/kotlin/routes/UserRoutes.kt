package org.example.routes

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.example.model.Users
import org.h2.engine.User
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.awt.TrayIcon

@Serializable
data class UserDTO(
	val id: Int,
	val name: String,
	val email: String?,
	val about: String?
)

fun Application.userRoutes() {
	install(ContentNegotiation) {
		json(Json { prettyPrint = true })
	}

	Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")

	transaction {
		SchemaUtils.createMissingTablesAndColumns(Users)

		val id = Users.insertAndGetId {
			it[name] = "Volera"
			it[about] = "Lorem ipsum"
			it[email] = "volera@gmail.com"
		}

		Users.selectAll().forEach {
			println("${it[Users.id]}, ${it[Users.name]}, ${it[Users.about]}, ${it[Users.email]}")
		}
	}

	routing {
		route("/user") {
			get {
				val id = call.parameters["id"] ?: return@get call.respond(
					HttpStatusCode.BadRequest,
					mapOf("id" to "is null"),
				)

				var user: ResultRow? = null
				transaction {
					println(Users.selectAll().count())
					user = Users.select { Users.id eq id.toInt() }.firstOrNull()
				}

				if (user == null) {
					call.respond(
						HttpStatusCode.NotFound,
						mapOf("object" to "not found"),
					)
				} else {
					call.respond(
						UserDTO(
							user!![Users.id].value,
							user!![Users.name],
							user!![Users.email],
							user!![Users.about]
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
		}
	}
}