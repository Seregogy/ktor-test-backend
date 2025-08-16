package org.example.dto

import kotlinx.serialization.Serializable
import org.example.model.ArtistEntity
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class BaseArtist(
	val id: String = "",
	val name: String = "unknown artist",
	val imageUrl: String? = ""
)

fun ArtistEntity.toBaseDTO(): BaseArtist {
	return BaseArtist(
		id = id.value.toString(),
		name = name,
		imageUrl = transaction { imagesUrl.firstOrNull()?.imageUrl } ?: ""
	)
}

fun ArtistEntity.toFullDTO() {
	TODO()
}