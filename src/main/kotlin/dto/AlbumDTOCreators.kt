package org.example.dto

import kotlinx.serialization.Serializable
import org.example.model.AlbumEntity

@Serializable
data class BaseAlbum(
	val id: String = "",
	val name: String = "",
	val imageUrl: String = "",
)

fun AlbumEntity.toBaseDTO(): BaseAlbum {
	return BaseAlbum(
		id = id.value.toString(),
		name = name,
		imageUrl = imageUrl ?: ""
	)
}

fun AlbumEntity.toFullDTO() {
	TODO()
}