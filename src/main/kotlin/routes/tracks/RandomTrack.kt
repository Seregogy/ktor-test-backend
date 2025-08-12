package org.example.routes.tracks

import io.ktor.server.application.call
import io.ktor.server.plugins.origin
import io.ktor.server.request.uri
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.response.respondText
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.example.model.TrackEntity
import org.example.model.TracksTable
import org.jetbrains.exposed.sql.Random
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
private data class Track(
	val id: String = "",
	val name: String = "unknown",
	val durationSeconds: Int = 0,
	val lyrics: String? = "",
	val indexInAlbum: Int = 0,
	val listening: Int? = 0,
	val isExplicit: Boolean? = false,
	val audioUrl: String = ""
)

@Serializable
private data class Album(
	val id: String = "",
	val name: String = "",
	val imageUrl: String = "",
)

@Serializable
private data class Artist(
	val id: String = "",
	val name: String = "unknown artist",
	val imageUrl: String? = ""
)

@Serializable
private data class RandomTrackResponse(
	val track: Track = Track(),
	val album: Album = Album(),
	val artist: List<Artist> = listOf()
)

fun Route.getRandomTrack() {
	get("random") {
		val track = transaction {
			TracksTable.selectAll().orderBy(Random()).limit(1).first().let {
				TrackEntity.wrapRow(it)
			}
		}

		transaction { track.listening += 1 }

		val album = transaction { track.album }
		val artist = transaction { track.artists.toList() }.map {
			it.run {
				Artist(
					id.value.toString(),
					name = name,
					imageUrl = transaction { imagesUrl.firstOrNull()?.imageUrl }
				)
			}
		}

		call.respond(
			RandomTrackResponse(
				track = Track(
					id = track.id.value.toString(),
					name = track.name,
					durationSeconds = track.durationSeconds,
					lyrics = track.lyrics,
					indexInAlbum = track.indexInAlbum,
					listening = track.listening,
					isExplicit = track.isExplicit,
					audioUrl = call.request.origin.let {
						"${it.scheme}://${it.serverHost}:${it.serverPort}/audio/${track.id}.mp3"
					}
				),
				album = Album(
					id = album.id.value.toString(),
					name = album.name,
					imageUrl = album.imageUrl ?: "",


				),
				artist = artist
			)
		)
	}
}