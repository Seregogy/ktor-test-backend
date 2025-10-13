package org.example.model

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.UUID

/*
Source data:

data class Track(
	val id: Int = 0,
	val albumId: Int = 0,
	val name: String = "",
	val seconds: Int = 0,
	val lyrics: String = "",
	val artistsId: List<Int> = listOf()
)*/

object TracksTable : UUIDTable("TRACKS_TABLE") {
	val albumId = reference("albumId", AlbumsTable)
	val name = text("name")
	val durationSeconds  = integer("durationSeconds ").default(0)
	val indexInAlbum = integer("index_in_album").default(0)
	val listening = integer("listening").default(0)
	val isExplicit = bool("isExplicit").default(false)
}

object ArtistsOnTrackTable : UUIDTable("ARTISTS_ON_TRACK_TABLE") {
	val track = reference("track", TracksTable)
	val artist = reference("artist", ArtistsTable)
}

object GenresTable : UUIDTable("TRACKS_GENRES") {
	var genreName = text("genre_name")
}

object TracksToGenreTable : UUIDTable("TRACKS_TO_GENRE_TABLE") {
	val track = reference("track", TracksTable)
	val genre = reference("genre", GenresTable)
}

object LyricsTable : UUIDTable("LYRICS_TABLE") {
	val plainText = text("plain_text")
	val syncedText = text("synced_text")

	val track = reference("track", TracksTable).uniqueIndex()
}

class LyricsEntity(id: EntityID<UUID>) : UUIDEntity(id) {
	companion object : UUIDEntityClass<LyricsEntity>(LyricsTable)

	var plainText by LyricsTable.plainText
	var syncedText by LyricsTable.syncedText

	val track by TrackEntity referencedOn LyricsTable.track
}

class TrackGenreEntity(id: EntityID<UUID>) : UUIDEntity(id) {
	companion object : UUIDEntityClass<TrackGenreEntity>(GenresTable)

	var name by GenresTable.genreName
	var tracks by TrackEntity via TracksToGenreTable
}

class TrackEntity(id: EntityID<UUID>) : UUIDEntity(id) {
	companion object : UUIDEntityClass<TrackEntity>(TracksTable)

	var name by TracksTable.name
	var durationSeconds by TracksTable.durationSeconds
	var indexInAlbum by TracksTable.indexInAlbum
	var listening by TracksTable.listening
	var isExplicit by TracksTable.isExplicit

	val lyrics by LyricsEntity referrersOn LyricsTable.track
	var album by AlbumEntity referencedOn TracksTable.albumId
	var artists by ArtistEntity via ArtistsOnTrackTable

	var genres by TrackGenreEntity via TracksToGenreTable
}