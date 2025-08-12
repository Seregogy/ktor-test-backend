package org.example.databaseAccessor

import org.example.model.AlbumEntity
import org.example.model.ArtistEntity
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.transactions.transaction
import java.nio.file.Path
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name

fun addAlbumToDb(
	artist: ArtistEntity,
	albumPath: Path
): AlbumEntity {
	val albumName = albumPath.last().name
	println("\t$albumName")

	val album = transaction {
		AlbumEntity.new {
			name = albumName
			artists = SizedCollection(artist)
		}
	}

	albumPath.listDirectoryEntries().forEachIndexed { i, it ->
		addTrackToDb(
			album,
			artist,
			it,
			i
		)
	}

	return album
}