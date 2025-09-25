package org.example.routes.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTCreator
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.example.model.UserEntity
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*
import kotlin.time.Duration.Companion.days
import kotlin.time.DurationUnit

@Serializable
data class UpdateAccessTokenRequest(
	val refreshToken: String
)

@Serializable
data class UpdateAccessTokensResponse(
	val accessToken: String,
	val refreshToken: String
)

fun Route.updateAccessToken(tokenBuilder: JWTCreator.Builder) {
	post("/refresh-token") {
		val request = call.receive<UpdateAccessTokenRequest>()

		val id = JWT
			.require(Algorithm.HMAC256(secret))
			.withIssuer(issuer)
			.build()
			.verify(request.refreshToken)
			.claims["id"]?.asString()

		println(id)

		transaction { UserEntity.findById(UUID.fromString(id)) }?.let { user ->
			if (user.refreshToken != request.refreshToken) {
				return@post call.respond(
					status = HttpStatusCode.Forbidden,
					message = mapOf(
						"error" to "invalid token"
					)
				)
			}

			suspendedTransactionAsync {
				val accessToken = tokenBuilder
					.withClaim("id", user.id.toString())
					.withExpiresAt(Date(System.currentTimeMillis().plus(30.days.toInt(DurationUnit.MILLISECONDS))))
					.sign(Algorithm.HMAC256(secret))

				val refreshToken = tokenBuilder
					.withClaim("id", user.id.toString())
					.withExpiresAt(Date(Long.MAX_VALUE))
					.sign(Algorithm.HMAC256(secret))

				user.refreshToken = refreshToken

				call.respond(
					status = HttpStatusCode.OK,
					message = UpdateAccessTokensResponse(
						accessToken = accessToken,
						refreshToken = refreshToken
					)
				)
			}
		}
	}
}