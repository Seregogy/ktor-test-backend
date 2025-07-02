package org.example

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.example.model.Users
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureRouting() {
	install(ContentNegotiation) {
		json(Json { prettyPrint = true })
	}

	val db = Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")

	transaction(db) {
		SchemaUtils.create(Users)

		val firstId = Users.insert {
			it[name] = "Volera"
			it[about] = "Lorem ipsum"
			it[email] = "volera@gmail.com"
		} get Users.id

		println(firstId)
	}

	routing {
		route("/user") {
			get {
				val id = call.parameters["id"] ?: return@get call.respondText(
					"{\"id\" : \"is null\"}",
					contentType = ContentType.Application.Json,
					status = HttpStatusCode.BadRequest
				)

				println(call.parameters["name"] ?: "parameter name not specified")

				val user = Users.select { Users.id eq id.toInt() }.firstOrNull() ?: return@get call.respondText(
					"{\"object\" : \"not found\"}",
					status = HttpStatusCode.NotFound
				)

				call.respond(user)
			}

			post {
				val user = transaction {
					Users.insert {

					}
				}

				call.respond(HttpStatusCode.Created, user)
			}
		}
	}
}

