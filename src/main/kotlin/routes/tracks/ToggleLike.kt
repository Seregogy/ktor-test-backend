package org.example.routes.tracks

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.example.model.TrackEntity
import org.example.model.UserEntity
import org.example.model.UsersFavoriteTracksTable
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

@Serializable
data class ToggleLikeResponse(
	val trackId: String,
	val liked: Boolean
)

fun Route.toggleLike() {
	authenticate("auth-jwt") {
		post("{trackId}/like") {
			val trackId = call.parameters["trackId"] ?: return@post call.respond(
				status = HttpStatusCode.BadRequest,
				mapOf(
					"trackId" to "not stated"
				)
			)

			val principal = call.principal<JWTPrincipal>()
			val userId = principal?.payload?.getClaim("id")?.asString() ?: return@post call.respond(
				status = HttpStatusCode.Forbidden,
				message = mapOf(
					"access token payload" to "data not found"
				)
			)

			val track = transaction {
				TrackEntity.findById(UUID.fromString(trackId))
			} ?: return@post call.respond(
				status = HttpStatusCode.NotFound,
				message = mapOf(
					"trackId" to "invalid"
				)
			)

			println(userId)

			val user = transaction {
				UserEntity.findById(UUID.fromString(userId))
			} ?: return@post call.respond(
				status = HttpStatusCode.NotFound,
				message = mapOf(
					"access token payload" to "invalid"
				)
			)

			//TODO: попробовать как-то это разобрать, потому что тут происходит какой-то сюр
			var liked = false
			transaction {
				liked = UsersFavoriteTracksTable.selectAll()
					.where { (UsersFavoriteTracksTable.user eq user.id) and (UsersFavoriteTracksTable.track eq track.id) }
					.count() > 0

				if (liked) {
					UsersFavoriteTracksTable.deleteWhere {
						(UsersFavoriteTracksTable.user eq user.id) and (UsersFavoriteTracksTable.track eq track.id)
					}
				} else {
					user.favoriteTracks = SizedCollection(user.favoriteTracks.toList().plus(track))
				}
			}

			call.respond(
				status = HttpStatusCode.OK,
				message = ToggleLikeResponse(
					trackId = trackId,
					liked = !liked
				)
			)
		}
	}
}