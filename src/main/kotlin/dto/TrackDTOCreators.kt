package org.example.dto

import kotlinx.serialization.Serializable
import org.example.model.TrackEntity
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
open class BaseTrack(
	val id: String = "",
	val name: String = "",
	val imageUrl: String? = "",
	val indexInAlbum: Int = 0
)

fun TrackEntity.toBaseDTO() : BaseTrack {
	return BaseTrack(
		id = id.value.toString(),
		name = name,
		imageUrl = transaction { album.imageUrl },
		indexInAlbum = indexInAlbum
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
		audioUrl = audioUrl
	)
}