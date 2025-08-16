package org.example.routes.tracks

import io.ktor.server.application.call
import io.ktor.server.plugins.origin
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.example.model.TrackEntity
import org.example.model.TracksTable
import org.jetbrains.exposed.sql.Random
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class Track(
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
data class Album(
	val id: String = "",
	val name: String = "",
	val imageUrl: String = "",
)

@Serializable
data class Artist(
	val id: String = "",
	val name: String = "unknown artist",
	val imageUrl: String? = ""
)

@Serializable
data class TrackResponse(
	val track: Track = Track(),
	val album: Album = Album(),
	val artists: List<Artist> = listOf()
)

fun Route.getRandomTrack() {
	get("random") {
		val track = transaction {
			TracksTable.selectAll().orderBy(Random()).limit(1).first().let {
				TrackEntity.wrapRow(it)
			}
		}

		call.respondRedirect("/api/v1/tracks/${track.id.value}")
	}
}