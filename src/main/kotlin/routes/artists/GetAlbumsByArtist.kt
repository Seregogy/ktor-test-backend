package org.example.routes.artists

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import kotlinx.serialization.Serializable
import org.example.dto.BaseAlbum
import org.example.dto.toBaseDTO
import org.example.model.ArtistEntity
import org.example.tools.tryParseUUIDFromString
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class GetAlbumsByArtistResponse(
	val albums: List<BaseAlbum> = listOf()
)

fun Route.getAlbumsFromArtist() {
	get("{id}/albums") {
		val artistId = call.parameters["id"]?.let {
			tryParseUUIDFromString(it)
		} ?: return@get call.respond(
			status = HttpStatusCode.BadRequest,
			message = mapOf(
				"id" to "not stated or invalid"
			)
		)

		val artist = transaction {
			ArtistEntity.findById(artistId)
		} ?: return@get call.respond(
			status = HttpStatusCode.BadRequest,
			message = mapOf(
				"id" to "invalid"
			)
		)

		call.respond(GetAlbumsByArtistResponse(
			albums = transaction { artist.albums.map { it.toBaseDTO() } }
		))
	}
}