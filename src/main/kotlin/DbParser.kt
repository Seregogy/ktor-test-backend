package org.example

import org.example.databaseAccessor.addTrackToDb
import org.example.model.AlbumEntity
import org.example.model.AlbumsTable
import org.example.model.ArtistEntity
import org.example.model.ArtistsTable
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.io.path.Path
import kotlin.io.path.listDirectoryEntries

fun main() {
	connectToDatabase()

	val artist = transaction {
		ArtistEntity.find {
			ArtistsTable.name eq "Post Malone"
		}.first()
	}

	val album = transaction {
		AlbumEntity.find {
			AlbumsTable.name eq "beerbongs & bentleys"
		}.first()
	}

	println(artist.name)
	println(album.name)

	Path("src/files/Twelve Carat Toothache/").listDirectoryEntries().forEachIndexed { index, path ->
		addTrackToDb(
			artist = artist,
			album = album,
			trackPath = path,
			index = index + 5
		)
	}
}