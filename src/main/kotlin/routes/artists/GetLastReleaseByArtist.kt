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
data class GetLastReleaseByArtistResponse(
	val lastAlbum: Album,
	val releaseDate: Long
)

fun Route.getLastReleaseByArtist() {
	get("{id}/albums/latest") {
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

		val release = transaction {
			artist.albums.maxByOrNull { it.releaseDate }
		} ?: return@get call.respond(
			status = HttpStatusCode.NotFound,
			message = mapOf(
				"release" to "not found"
			)
		)

		call.cacheControl(30.minutes())
		call.respond(GetLastReleaseByArtistResponse(
			lastAlbum = release.toBaseDTO(),
			releaseDate = release.releaseDate
		))
	}
}