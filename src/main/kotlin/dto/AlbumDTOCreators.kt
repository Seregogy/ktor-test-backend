package org.example.dto

import kotlinx.serialization.Serializable
import org.example.model.AlbumEntity
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class Album(
	val id: String = "",
	val name: String = "",
	val imageUrl: String = "",
	val artists: List<Artist> = listOf()
)

fun AlbumEntity.toBaseDTO(): Album {
	return Album(
		id = id.value.toString(),
		name = name,
		imageUrl = imageUrl ?: "",
		artists = transaction { artists.map { it.toBaseDTO() } }
	)
}

@Serializable
data class FullAlbum(
	val id: String = "",
	val name: String = "",
	val imageUrl: String? = null,
	val artists: List<Artist> = listOf(),
	val likes: Int = 0,
	val listening: Int = 0,
	val releaseDate: Long = 0,
	val label: String? = null
)

fun AlbumEntity.toFullDTO() : FullAlbum {
	return FullAlbum(
		id = id.value.toString(),
		name = name,
		likes = likes,
		listening = transaction { tracks.sumOf { it.listening } },
		releaseDate = releaseDate,
		imageUrl = imageUrl,
		label = label,
		artists = transaction { artists.map { it.toBaseDTO() } }
	)
}