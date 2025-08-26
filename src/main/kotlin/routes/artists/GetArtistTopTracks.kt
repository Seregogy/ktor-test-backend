package org.example.routes.artists

import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import kotlinx.serialization.Serializable
import org.example.dto.BaseTrack
import org.example.dto.toBaseDTO
import org.example.model.ArtistEntity
import org.example.tools.cacheControl
import org.example.tools.minutes
import org.example.tools.tryParseUUIDFromString
import org.jetbrains.exposed.sql.transactions.transaction

const val tracksLimitDefault = 9

@Serializable
data class GetArtistTopTracksResponse(
	val tracks: List<BaseTrack> = listOf()
)

fun Route.getArtistTopTracks() {
	get("{id}/tracks/top") {
		val tracksLimit = call.queryParameters["limit"]?.toInt() ?: tracksLimitDefault

		val artistId = call.parameters["id"]?.let {
			tryParseUUIDFromString(it)
		} ?: return@get call.respond(
			status = HttpStatusCode.BadRequest,
			message = mapOf(
				"id" to "not stated"
			)
		)

		val artist = transaction {
			ArtistEntity.findById(artistId)
		} ?: return@get call.respond(
			status = HttpStatusCode.NotFound,
			message = mapOf(
				"id" to "invalid"
			)
		)

		val tracks = transaction {
			artist.albums.flatMap { it.tracks }.sortedByDescending { it.listening }.take(tracksLimit).map { it.toBaseDTO() }
		}

		call.cacheControl(5.minutes())
		call.respond(GetArtistTopTracksResponse(tracks))
	}
}