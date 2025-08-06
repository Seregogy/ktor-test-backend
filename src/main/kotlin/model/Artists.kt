package org.example.model

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.UUID

/*
Source data:

data class Artist(
	val id: Int = 0,
	val name: String = "",
	val about: String = "",
	val listeningInMonth: Int = 0,
	val likes: Int = 0,
	val bestTracks: List<Int> = listOf(),
	val albums: List<Int> = listOf(),
	val socialMedia: Map<String, String> = mapOf(),
	val imagesUrl: List<Pair<String, Int>> = listOf()
)*/

object ArtistsTable : UUIDTable("ARTISTS") {
	val name = text("name")
	val about = text("about").nullable()
	val listeningInMonth = integer("listeningInMonth").default(0)
	val likes = integer("likes").default(0)
}

object ImagesUrlTable : UUIDTable("IMAGES_URLS_TABLE") {
	val artist = reference("artist", ArtistsTable)
	val imageUrl = text("imageUrl")
	val primaryColor = integer("primaryColor")
}

object SocialMediasTable : UUIDTable("SOCIAL_MEDIAS_TABLE") {
	val artist = reference("artist", ArtistsTable)
	val socialMediaName = text("social_media_name")
	val link = text("link")
}

object ArtistAlbumsTable : IntIdTable("ARTIST_ALBUMS_TABLE") {
	val artist = reference("artist", ArtistsTable)
	val album = reference("album", AlbumsTable)
}

class ArtistEntity(id: EntityID<UUID>) : UUIDEntity(id) {
	companion object : UUIDEntityClass<ArtistEntity>(ArtistsTable)

	var name by ArtistsTable.name
	var about by ArtistsTable.about
	var listeningInMonth by ArtistsTable.listeningInMonth
	var likes by ArtistsTable.likes

	val socialMedias by SocialMediaEntity referrersOn SocialMediasTable.artist
	var albums by AlbumEntity via ArtistAlbumsTable
	val imagesUrl by ArtistImageEntity referrersOn ImagesUrlTable.artist
}

class SocialMediaEntity(id: EntityID<UUID>) : UUIDEntity(id) {
	companion object : UUIDEntityClass<SocialMediaEntity>(SocialMediasTable)

	var socialMediaName by SocialMediasTable.socialMediaName
	var link by SocialMediasTable.link

	var artist by ArtistEntity referencedOn SocialMediasTable.artist
}

class ArtistImageEntity(id: EntityID<UUID>) : UUIDEntity(id) {
	companion object : UUIDEntityClass<ArtistImageEntity>(ImagesUrlTable)

	var imageUrl by ImagesUrlTable.imageUrl
	var primaryColor by ImagesUrlTable.primaryColor

	var artist by ArtistEntity referencedOn ImagesUrlTable.artist
}