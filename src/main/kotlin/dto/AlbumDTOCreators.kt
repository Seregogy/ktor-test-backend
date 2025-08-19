package org.example.dto

import kotlinx.serialization.Serializable
import org.example.model.AlbumEntity
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class BaseAlbum(
	val id: String = "",
	val name: String = "",
	val imageUrl: String = "",
	val artists: List<BaseArtist> = listOf()
)

fun AlbumEntity.toBaseDTO(): BaseAlbum {
	return BaseAlbum(
		id = id.value.toString(),
		name = name,
		imageUrl = imageUrl ?: "",
		artists = transaction { artists.map { it.toBaseDTO() } }
	)
}

@Serializable
data class FullAlbum(
	val name: String = "",
	val likes: Int = 0,
	val listening: Int = 0,
	val releaseDate: Long = 0,
	val imageUrl: String? = null,
	val label: String? = null,
	val tracks: List<BaseTrack> = listOf(),
	val artists: List<BaseArtist> = listOf()
)

fun AlbumEntity.toFullDTO() : FullAlbum {
	return FullAlbum(
		name = name,
		likes = likes,
		listening = transaction { tracks.sumOf { it.listening } },
		releaseDate = releaseDate,
		imageUrl = imageUrl,
		label = label,
		tracks = transaction { tracks.map { it.toBaseDTO() } },
		artists = transaction { artists.map { it.toBaseDTO() } }
	)
}