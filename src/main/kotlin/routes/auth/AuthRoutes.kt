package org.example.routes.auth

import com.auth0.jwt.JWT
import io.ktor.server.application.*
import io.ktor.server.routing.*

val secret: String? = System.getenv("secret")
val issuer: String? = System.getenv("issuer")
val audience: String? = System.getenv("audience")

val jwtTokenBuilder = JWT.create()
	.withIssuer(issuer)
	.withAudience(audience)

fun Application.authRoutes() {
	routing {
		route("api/v1/auth") {
			register(jwtTokenBuilder)

			updateAccessToken(jwtTokenBuilder)
		}
	}
}