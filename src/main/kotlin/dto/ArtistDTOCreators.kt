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

fun ArtistEntity.toBaseDTO(): Artist {
	return Artist(
		id = id.value.toString(),
		name = name,
		about = about,
		imageUrl = transaction { imagesUrl.firstOrNull()?.imageUrl } ?: ""
	)
}

fun ArtistEntity.toFullDTO() : FullArtist {
	val images = transaction { imagesUrl }

	return FullArtist(
		id = id.value.toString(),
		name = name,
		about = about,
		listeningInMonth = transaction { albums.flatMap { it.tracks }.sumOf { it.listening } },
		likes = likes,
		images = transaction { images.map { it.imageUrl }.toList() },
		socialMedias = transaction { socialMedias.toList().map { SocialMedia(it.socialMediaName, it.link) } }
	)
}