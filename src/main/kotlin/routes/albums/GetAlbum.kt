package org.example.routes.albums

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.dto.toFullDTO
import org.example.model.AlbumEntity
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

fun Route.getAlbumById() {
	get("{id}") {
		val albumId = call.parameters["id"] ?: return@get call.respond(
			status = HttpStatusCode.BadRequest,
			message = mapOf(
				"id" to "not specified"
			)
		)

		val album = transaction {
			AlbumEntity.findById(UUID.fromString(albumId))
		} ?: return@get call.respond(
			status = HttpStatusCode.BadRequest,
			message = mapOf(
				"id" to "invalid"
			)
		)

		call.respond(album.toFullDTO())
	}
}