package org.example.routes.tracks

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import kotlinx.serialization.Serializable
import org.example.dto.FullTrack
import org.example.dto.toFullDTO
import org.example.model.TrackEntity
import org.example.model.TracksTable
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

@Serializable
data class GetTracksRequest(
	val tracks: List<String>
)

@Serializable
data class GetTracksResponse(
	val tracks: List<FullTrack>
)

fun Route.getTracks() {
	post("") {
		val request = call.receive<GetTracksRequest>()

		runCatching {
			 transaction {
				TrackEntity.find {
					TracksTable.id inList request.tracks.map { UUID.fromString(it) }
				}.map { track ->
					track.toFullDTO("${System.getenv("baseUrl")}audio/${track.id}.mp3")
				}
			}
		}.onSuccess {
			call.respond(GetTracksResponse(it))
		}.onFailure {
			return@post call.respond(
				status = HttpStatusCode.BadRequest,
				message = mapOf(
					"error" to "invalid request"
				)
			)
		}
	}
}