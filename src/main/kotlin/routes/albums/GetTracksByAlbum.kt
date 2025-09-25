package org.example.routes.albums

import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import org.example.dto.toBaseDTO
import org.example.dto.toFullDTO
import org.example.model.AlbumEntity
import org.example.tools.cacheControl
import org.example.tools.minutes
import org.example.tools.tryParseUUIDFromString
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.getTracksByAlbum() {
	get("{id}/tracks") {
		val albumId = call.parameters["id"]?.let {
			tryParseUUIDFromString(it)
		} ?: return@get call.respond(
			status = HttpStatusCode.BadRequest,
			message = mapOf(
				"error" to "id not stated"
			)
		)

		val album = transaction {
			AlbumEntity.findById(albumId)
		} ?: return@get call.respond(
			status = HttpStatusCode.BadRequest,
			message = mapOf(
				"error" to "invalid id"
			)
		)

		call.cacheControl(30.minutes())
		call.respond(
			transaction {
				mapOf(
					"tracks" to album.tracks.map { it.toBaseDTO("${System.getenv("baseUrl")}/audio/${it.id}.mp3") }
				)
			}
		)
	}
}