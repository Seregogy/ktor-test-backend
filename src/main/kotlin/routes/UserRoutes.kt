package org.example.routes

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Application.userRoutes() {
	routing {
		authenticate("auth-jwt") {
			route("api/v1/users") {

			}
		}
	}
}