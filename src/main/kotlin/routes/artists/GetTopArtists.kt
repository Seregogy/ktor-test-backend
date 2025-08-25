package org.example.routes.artists

import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import kotlinx.serialization.Serializable
import org.example.dto.BaseArtist
import org.example.dto.toBaseDTO
import org.example.model.ArtistEntity
import org.example.tools.cacheControl
import org.example.tools.minutes
import org.jetbrains.exposed.sql.transactions.transaction

private val defaulLimit = 5

@Serializable
data class GetTopArtistsResponse(
	val artists: List<BaseArtist>
)

//TODO: оптимизировать подсчёт прослушиваний
fun Route.getTopArtists() {
	get("top") {
		val limit = call.parameters["limit"]?.toInt() ?: defaulLimit

		val artists = transaction {
			ArtistEntity.all().sortedBy { artist ->
				artist.albums.sumOf { album ->
					album.tracks.sumOf { track ->
						track.listening
					}
				}
			}.map {
				it.toBaseDTO()
			}.reversed().take(limit)
		}

		call.cacheControl(10.minutes())
		call.respond(GetTopArtistsResponse(artists))
	}
}