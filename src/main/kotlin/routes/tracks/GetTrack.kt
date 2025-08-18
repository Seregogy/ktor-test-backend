package org.example.routes.tracks

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.origin
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.example.dto.BaseAlbum
import org.example.dto.BaseArtist
import org.example.dto.FullTrack
import org.example.dto.toBaseDTO
import org.example.dto.toFullDTO
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

		call.respond(
			track.toFullDTO(call.request.origin.let {
				"${it.scheme}://${it.serverHost}:${it.serverPort}/audio/${track.id}.mp3"
			})
		)
	}
}