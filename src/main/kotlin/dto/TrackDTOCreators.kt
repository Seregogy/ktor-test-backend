package org.example.dto

import kotlinx.serialization.Serializable
import org.example.model.TrackEntity
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
open class Track(
	val id: String = "",
	val name: String = "",
	val imageUrl: String? = "",
	val indexInAlbum: Int = 0
)

fun TrackEntity.toBaseDTO() : Track {
	return Track(
		id = id.value.toString(),
		name = name,
		imageUrl = transaction { album.imageUrl },
		indexInAlbum = indexInAlbum
	)
}

@Serializable
open class TrackWithArtists(
	val id: String = "",
	val name: String = "",
	val imageUrl: String? = "",
	val indexInAlbum: Int = 0,
	val artists: List<Artist> = listOf()
)

fun TrackEntity.toBaseDTOWithArtists() : TrackWithArtists {
	return TrackWithArtists(
		id = id.value.toString(),
		name = name,
		imageUrl = transaction { album.imageUrl },
		indexInAlbum = indexInAlbum,
		artists = artists.map { it.toBaseDTO() }
	)
}

@Serializable
class FullTrack(
	val id: String = "",
	val name: String = "",
	val imageUrl: String? = "",
	val indexInAlbum: Int = 0,
	val durationSeconds: Int = 0,
	val lyrics: String? = "",
	val listening: Int? = 0,
	val isExplicit: Boolean? = false,
	val audioUrl: String = "",
	val album: Album = Album()
)

fun TrackEntity.toFullDTO(audioUrl: String) : FullTrack {
	return FullTrack(
		id = id.value.toString(),
		name = name,
		imageUrl = transaction { album.imageUrl },
		indexInAlbum = indexInAlbum,
		durationSeconds = durationSeconds,
		lyrics = lyrics,
		listening = listening,
		isExplicit = isExplicit,
		audioUrl = audioUrl,
		album = transaction { album.toBaseDTO() }
	)
}