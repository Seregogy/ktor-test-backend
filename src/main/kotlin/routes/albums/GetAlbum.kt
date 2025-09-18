package org.example.routes.albums

import io.ktor.http.*
import io.ktor.http.content.CachingOptions
import io.ktor.server.plugins.cachingheaders.CachingHeaders
import io.ktor.server.plugins.cachingheaders.caching
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.dto.toFullDTO
import org.example.model.AlbumEntity
import org.example.tools.cacheControl
import org.example.tools.hours
import org.example.tools.minutes
import org.example.tools.tryParseUUIDFromString
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.getAlbumById() {
	get("{id}") {
		val albumId = call.parameters["id"]?.let {
			tryParseUUIDFromString(it)
		} ?: return@get call.respond(
			status = HttpStatusCode.BadRequest,
			message = mapOf(
				"id" to "not stated"
			)
		)

		val album = transaction {
			AlbumEntity.findById(albumId)
		} ?: return@get call.respond(
			status = HttpStatusCode.BadRequest,
			message = mapOf(
				"id" to "invalid"
			)
		)

		call.cacheControl(30.minutes())
		call.respond(album.toFullDTO())
	}
}