package org.example.routes.users

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Application.usersRoutes() {
	routing {
		authenticate("auth-jwt") {
			route("api/v1/users") {

			}
		}
	}
}