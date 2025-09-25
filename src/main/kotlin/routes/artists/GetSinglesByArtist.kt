package org.example.routes.artists

import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import kotlinx.serialization.Serializable
import org.example.dto.Album
import org.example.dto.toBaseDTO
import org.example.model.ArtistEntity
import org.example.tools.cacheControl
import org.example.tools.minutes
import org.example.tools.tryParseUUIDFromString
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
private data class GetSinglesByArtistResponse(
	val singles: List<Album> = listOf()
)

fun Route.getSinglesByArtist() {
	get("{id}/singles") {
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
		call.respond(GetSinglesByArtistResponse(
			singles = transaction { artist.albums.filter { it.tracks.count() == 1L }.map { it.toBaseDTO() } }
		))
	}
}