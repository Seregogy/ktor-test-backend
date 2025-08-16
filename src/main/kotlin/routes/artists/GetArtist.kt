package org.example.routes.artists

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import kotlinx.serialization.Serializable
import org.example.dto.BaseTrack
import org.example.dto.FullArtist
import org.example.dto.toBaseDTO
import org.example.dto.toFullDTO
import org.example.model.ArtistEntity
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

@Serializable
data class GetArtistResponse(
	val artist: FullArtist = FullArtist(),
	val topTracks: List<BaseTrack> = listOf()
)

fun Route.getArtist() {
	get("{id}") {
		val id = call.parameters["id"] ?: return@get call.respond(
			status = HttpStatusCode.BadRequest,
			message = mapOf(
				"id" to "not stated"
			)
		)

		val tracks = call.parameters["tracks"]?.toInt() ?: 10

		val artist = transaction {
			ArtistEntity.findById(UUID.fromString(id))
		} ?: return@get call.respond(
			status = HttpStatusCode.NotFound,
			message = mapOf(
				"id" to "invalid"
			)
		)

		call.respond(
			message = GetArtistResponse(
				artist = artist.toFullDTO(),
				topTracks = transaction { artist.albums.flatMap { it.tracks }.sortedByDescending { it.listening }.take(tracks).map { it.toBaseDTO() } }
			)
		)
	}
}