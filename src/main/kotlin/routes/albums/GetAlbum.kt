package org.example.routes.albums

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import kotlinx.serialization.Serializable
import org.example.model.AlbumEntity
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

@Serializable
private data class Artist(
	val id: String,
	val name: String,
	val imageUrl: String
)

@Serializable
private data class Track(
	val id: String,
	val name: String,
	val isExplicit: Boolean
)

@Serializable
private data class Album(
	val name: String,
	val likes: Int,
	val listening: Int,
	val releaseDate: Long,
	val imageUrl: String?,
	val label: String?,
	val tracks: List<Track>,
	val artists: List<Artist>
)

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

		val tracks = transaction { album.tracks.toList() }.map {
			it.let {
				Track(
					id = it.id.value.toString(),
					name = it.name,
					isExplicit = it.isExplicit
				)
			}
		}


		val artists = transaction { album.artists.toList() }.map {
			it.let {
				Artist(
					id = it.id.value.toString(),
					name = it.name,
					imageUrl = it.imagesUrl.first().imageUrl
				)
			}
		}

		call.respond(
			Album(
				name = album.name,
				likes = album.likes,
				listening = album.listening,
				releaseDate = album.releaseDate,
				imageUrl = album.imageUrl,
				label = album.label,
				tracks = tracks,
				artists = artists
			)
		)

		TODO("implement on client")
	}
}