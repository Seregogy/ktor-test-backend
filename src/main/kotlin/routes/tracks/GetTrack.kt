package org.example.routes.tracks

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.origin
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.model.TrackEntity
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*


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

		val album = transaction { track.album }
		val artist = transaction { track.artists.toList() }.map {
			it.run {
				Artist(
					id.value.toString(),
					name = name,
					imageUrl = transaction { imagesUrl.firstOrNull()?.imageUrl }
				)
			}
		}

		call.respond(
			TrackResponse(
				track = Track(
					id = track.id.value.toString(),
					name = track.name,
					durationSeconds = track.durationSeconds,
					lyrics = track.lyrics,
					indexInAlbum = track.indexInAlbum,
					listening = track.listening,
					isExplicit = track.isExplicit,
					audioUrl = call.request.origin.let {
						"${it.scheme}://${it.serverHost}:${it.serverPort}/audio/${track.id}.mp3"
					}
				),
				album = Album(
					id = album.id.value.toString(),
					name = album.name,
					imageUrl = album.imageUrl ?: "",


					),
				artists = artist
			)
		)
	}
}