package org.example.databaseAccessor

import org.example.model.AlbumEntity
import org.example.model.ArtistEntity
import org.example.model.TrackEntity
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.nio.file.Path
import kotlin.io.path.name
import kotlin.io.path.pathString

fun addTrackToDb(
	album: AlbumEntity,
	artist: ArtistEntity,
	trackPath: Path,
	index: Int
) : TrackEntity? {
	val trackName = trackPath.last().name.split(".").dropLast(1).joinToString("")

	val track = transaction {
		TrackEntity.new {
			name = trackName
			indexInAlbum = index

			this.album = album
			artists = SizedCollection(artist)
		}
	}

	println("\t\t$trackName")

	File(trackPath.pathString).renameTo(File("src/files/audio/${track.id}.${trackPath.pathString.split('.').last()}"))

	return track
}