package org.example.databaseAccessor

import org.example.model.ArtistEntity
import org.jetbrains.exposed.sql.transactions.transaction
import java.nio.file.Path
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name

fun addArtistToDb(
	path: Path
) : ArtistEntity {
	val artistName = path.last().name
	println(artistName)

	val artist: ArtistEntity = transaction {
		ArtistEntity.new {
			name = artistName
		}
	}

	path.listDirectoryEntries().forEach {
		addAlbumToDb(
			artist,
			it
		)
	}

	return artist
}