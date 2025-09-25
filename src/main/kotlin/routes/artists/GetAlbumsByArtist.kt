package org.example.routes.artists

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.example.dto.Album
import org.example.dto.toBaseDTO
import org.example.model.ArtistEntity
import org.example.tools.cacheControl
import org.example.tools.minutes
import org.example.tools.tryParseUUIDFromString
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
private data class GetAlbumsByArtistResponse(
	val albums: List<Album> = listOf()
)

fun Route.getAlbumsByArtist() {
	get("{id}/albums") {
		val artistId = call.parameters["id"]?.let {
			tryParseUUIDFromString(it)
		} ?: return@get call.respond(
			status = HttpStatusCode.BadRequest,
			message = mapOf(
				"error" to "id not stated or invalid"
			)
		)

		val artist = transaction {
			ArtistEntity.findById(artistId)
		} ?: return@get call.respond(
			status = HttpStatusCode.BadRequest,
			message = mapOf(
				"error" to "invalid id"
			)
		)

		call.cacheControl(30.minutes())
		call.respond(GetAlbumsByArtistResponse(
			albums = transaction { artist.albums.filter { it.tracks.count() > 1 }.map { it.toBaseDTO() } }
		))
	}
}