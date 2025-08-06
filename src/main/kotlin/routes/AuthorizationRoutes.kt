package org.example.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import java.util.*
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit

@Serializable
data class LoginRequest(
	val login: String,
	val password: String
)

val secret = System.getenv("secret")
val issuer = System.getenv("issuer")
val audience = System.getenv("audience")

fun Application.authorizationRoutes() {
	routing {
		route("api/v1/auth") {
			post("/login") {
				val user = call.receive<LoginRequest>()

				val tokenBuilder = JWT.create()
					.withIssuer(issuer)
					.withAudience(audience)
					.withClaim("login", user.login)

				val accessToken = tokenBuilder
					.withExpiresAt(Date(System.currentTimeMillis().plus(1.minutes.toInt(DurationUnit.MILLISECONDS))))
					.sign(Algorithm.HMAC256(secret))

				val refreshToken = tokenBuilder
					.withExpiresAt(Date(System.currentTimeMillis().plus(365.days.toInt(DurationUnit.MILLISECONDS))))
					.sign(Algorithm.HMAC256(secret))

				call.respond(
					hashMapOf(
						"accessToken" to accessToken,
						"refreshToken" to refreshToken
					)
				)
			}

			post("/refresh") {
				call.respond(
					HttpStatusCode.NotImplemented
				)
			}
		}
	}
}