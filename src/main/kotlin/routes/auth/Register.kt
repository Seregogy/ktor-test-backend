package org.example.routes.auth

import com.auth0.jwt.JWTCreator
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.example.model.UserEntity
import org.example.model.UsersTable
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*
import kotlin.time.Duration.Companion.days
import kotlin.time.DurationUnit

@Serializable
data class RegisterRequest(
	val name: String = "",
	val email: String = "",
	val password: String = ""
)

@Serializable
data class RegisterResponse(
	val id: String,
	val name: String,
	val accessToken: String,
	val refreshToken: String
)

fun Route.register(tokenBuilder: JWTCreator.Builder) {
	post("/register") {
		val request = call.receive<RegisterRequest>()

		if (transaction { UserEntity.find { UsersTable.email eq request.email }.any() }) {
			return@post call.respond(
				status = HttpStatusCode.Conflict,
				message = mapOf(
					"user" to "user with same email already exists"
				)
			)
		}

		validateRequest(request)?.let {
			return@post call.respond(
				status = HttpStatusCode.BadRequest,
				message = it
			)
		}

		suspendedTransactionAsync {
			val user = UserEntity.new {
				name = request.name
				email = request.email
				passwordHash = request.password.hashCode().toString()
			}

			val accessToken = tokenBuilder
				.withClaim("id", user.id.value.toString())
				.withExpiresAt(Date(System.currentTimeMillis().plus(30.days.toInt(DurationUnit.MILLISECONDS))))
				.sign(Algorithm.HMAC256(secret))

			val refreshToken = tokenBuilder
				.withClaim("id", user.id.value.toString())
				.withExpiresAt(Date(Long.MAX_VALUE))
				.sign(Algorithm.HMAC256(secret))

			user.refreshToken = refreshToken

			call.respond(
				status = HttpStatusCode.Created,
				message = RegisterResponse(
					id = user.id.value.toString(),
					name = user.name,
					accessToken = accessToken,
					refreshToken = user.refreshToken
				)
			)
		}
	}
}

fun validateRequest(registerRequest: RegisterRequest): Map<String, String>? {
	val errors = mutableMapOf<String, String>()

	if ((registerRequest.name.length in 1..32).not())
		errors["name"] = "name is to short or to long"

	if (validateEmail(registerRequest.email).not())
		errors["email"] = "invalid email"

	if (registerRequest.password.length in 0..8)
		errors["password"] = "password is to short"

	return errors.ifEmpty {
		null
	}
}

//TODO: сделать нормальную валидацию емаеила
fun validateEmail(email: String): Boolean = email.length in 5..256