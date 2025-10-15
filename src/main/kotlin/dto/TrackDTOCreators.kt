package org.example.dto

import kotlinx.serialization.Serializable
import org.example.model.LyricsEntity
import org.example.model.TrackEntity
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class Track(
	val id: String = "",
	val name: String = "",
	val imageUrl: String? = "",
	val indexInAlbum: Int = 0,
	val audioUrl: String = "",
)

fun TrackEntity.toBaseDTO(audioUrl: String) : Track = transaction {
	Track(
		id = this@toBaseDTO.id.value.toString(),
		name = name,
		imageUrl = album.imageUrl,
		indexInAlbum = indexInAlbum,
		audioUrl = audioUrl
	)
}

@Serializable
data class TrackWithArtists(
	val id: String = "",
	val name: String = "",
	val imageUrl: String? = "",
	val indexInAlbum: Int = 0,
	val artists: List<Artist> = listOf(),
	val audioUrl: String = "",
)

fun TrackEntity.toBaseDTOWithArtists(audioUrl: String) : TrackWithArtists = transaction {
	TrackWithArtists(
		id = this@toBaseDTOWithArtists.id.value.toString(),
		name = name,
		imageUrl = album.imageUrl,
		indexInAlbum = indexInAlbum,
		artists = artists.map { it.toBaseDTO() },
		audioUrl = audioUrl
	)
}

@Serializable
data class FullTrack(
	val id: String = "",
	val name: String = "",
	val imageUrl: String? = "",
	val indexInAlbum: Int = 0,
	val durationSeconds: Int = 0,
	val hasLyrics: Boolean = false,
	val listening: Int? = 0,
	val isExplicit: Boolean? = false,
	val audioUrl: String = "",
	val album: Album = Album()
)

fun TrackEntity.toFullDTO(audioUrl: String) : FullTrack = transaction {
	FullTrack(
		id = this@toFullDTO.id.value.toString(),
		name = name,
		imageUrl = album.imageUrl,
		indexInAlbum = indexInAlbum,
		durationSeconds = durationSeconds,
		hasLyrics = lyrics.firstOrNull() != null,
		listening = listening,
		isExplicit = isExplicit,
		audioUrl = audioUrl,
		album = album.toBaseDTO()
	)
}