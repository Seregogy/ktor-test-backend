package org.example.routes.tracks

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.model.TrackEntity
import org.example.tools.tryParseUUIDFromString
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.getAlbumByTrack() {
	get("{id}/album") {
		val trackId = call.parameters["id"]?.let {
			tryParseUUIDFromString(it)
		} ?: return@get call.respond(
			status = HttpStatusCode.BadRequest,
			message = mapOf(
				"id" to "not stated or invalid"
			)
		)

		val albumId = transaction {
			TrackEntity.findById(trackId)?.album?.id
		} ?: return@get call.respond(
			status = HttpStatusCode.NotFound,
			message = mapOf(
				"id" to "album id not found or track id is invalid"
			)
		)

		call.respondRedirect("/api/v1/albums/$albumId")
	}
}