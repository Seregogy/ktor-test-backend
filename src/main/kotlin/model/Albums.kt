package org.example.model

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.*

/*
Source data:

data class Album(
	val id: Int = 0,
	val artistsId: List<Int> = listOf(),
	val name: String = "",
	val likes: Int = 0,
	val tracksId: List<Int> = listOf(),
	val bestTracks: List<Int> = listOf(),
	val totalListening: Int = 0,
	val releaseDate: Long = 0,
	val imageUrl: String = "",
	val label: String = "",
	val primaryColor: Int = 0
)*/

object AlbumsTable : UUIDTable("ALBUMS_TABLE") {
	val name = text("name")
	val likes = integer("likes").default(0)
	val listening = integer("listening").default(0)
	val releaseDate = long("release_date").default(0)
	val label = text("label").nullable()
	val imageUrl = text("image_url").nullable()
	val primaryColor = integer("primary_color").default(0)
}

class AlbumEntity(id: EntityID<UUID>) : UUIDEntity(id) {
	companion object : UUIDEntityClass<AlbumEntity>(AlbumsTable)

	var name by AlbumsTable.name
	var likes by AlbumsTable.likes
	var listening by AlbumsTable.listening
	var releaseDate by AlbumsTable.releaseDate
	var label by AlbumsTable.label
	var imageUrl by AlbumsTable.imageUrl
	var primaryColor by AlbumsTable.primaryColor

	val tracks by TrackEntity referrersOn TracksTable.albumId
	var artists by ArtistEntity via ArtistAlbumsTable
}