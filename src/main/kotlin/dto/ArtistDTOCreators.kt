package org.example.dto

import kotlinx.serialization.Serializable
import org.example.model.ArtistEntity
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class SocialMedia(
	val name: String = "",
	val link: String = ""
)

@Serializable
data class Artist(
	val id: String = "",
	val name: String = "unknown artist",
	val about: String? = "unknown artist",
	val imageUrl: String? = ""
)

@Serializable
data class FullArtist(
	val id: String = "",
	val name: String = "unknown artist",
	val about: String? = "",
	val listeningInMonth: Int = 0,
	val likes: Int = 0,
	val images: List<String> = listOf(),
	val socialMedias: List<SocialMedia> = listOf()
)

fun ArtistEntity.toBaseDTO(): Artist = transaction {
	Artist(
		id = id,
		name = name,
		about = about,
		imageUrl = imagesUrl.firstOrNull()?.imageUrl ?: ""
	)
}

fun ArtistEntity.toFullDTO() : FullArtist = transaction {
	FullArtist(
		id = id,
		name = name,
		about = about,
		listeningInMonth = albums.flatMap { it.tracks }.sumOf { it.listening },
		likes = likes,
		images = imagesUrl.map { it.imageUrl }.toList(),
		socialMedias = socialMedias.toList().map { SocialMedia(it.socialMediaName, it.link) }
	)
}