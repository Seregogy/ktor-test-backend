package org.example.databaseAccessor

import org.example.model.ArtistEntity
import org.example.model.ArtistsTable
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.nio.file.Path
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name

fun addArtistToDb(
	path: Path
) : ArtistEntity {
	val artistName = path.last().name
	println(artistName)

	val artist: ArtistEntity = transaction {
		(ArtistEntity.find {
			ArtistsTable.name eq artistName
		}.firstOrNull() ?: {
			ArtistEntity.new {
				name = artistName
			}
		}) as ArtistEntity
	}

	path.listDirectoryEntries().forEach {
		println(it)

		addAlbumToDb(
			artist,
			it
		)
	}

	return artist
}