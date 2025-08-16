package org.example.routes.tracks

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.origin
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.example.dto.FullTrack
import org.example.dto.toBaseDTO
import org.example.dto.toFullDTO
import org.example.model.TrackEntity
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

@Serializable
data class TrackResponse(
	val track: FullTrack = FullTrack(),
	val album: BaseAlbum = BaseAlbum(),
	val artists: List<BaseArtist> = listOf()
)

fun Route.getTrack() {
	get("{id}") {
		val trackId = call.parameters["id"] ?: return@get call.respond(
			status = HttpStatusCode.BadRequest,
			message = mapOf(
				"id" to "not stated"
			)
		)

		val track = transaction {
			TrackEntity.findById(UUID.fromString(trackId))
		} ?: return@get call.respond(
			status = HttpStatusCode.NotFound,
			message = mapOf(
				"id" to "invalid"
			)
		)

		transaction { track.listening += 1 }

		val album = transaction { track.album.toBaseDTO() }
		val artist = transaction { track.artists.toList() }.map { it.To
			it.run {
				BaseArtist(
					id.value.toString(),
					name = name,
					imageUrl = transaction { imagesUrl.firstOrNull()?.imageUrl }
				)
			}
		}

		call.respond(
			TrackResponse(
				track = track.toFullDTO(
					call.request.origin.let {
						"${it.scheme}://${it.serverHost}:${it.serverPort}/audio/${track.id}.mp3"
					}
				),
				album = album,
				artists = artist
			)
		)
	}
}