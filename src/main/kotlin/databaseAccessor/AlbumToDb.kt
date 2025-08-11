package org.example.databaseAccessor

import org.example.model.AlbumEntity
import org.example.model.ArtistEntity
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.transactions.transaction
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name

fun addAlbumToDb(
	artistEntity: ArtistEntity,
	albumPath: Path
): AlbumEntity {
	val albumName = albumPath.last().name
	println(albumName)

	val album = transaction {
		AlbumEntity.new {
			name = albumName
			artists = SizedCollection(artistEntity)
		}
	}

	println(album)

	println("\t$albumPath, ${albumPath.exists()}")
	albumPath.listDirectoryEntries().forEach {
		println(it)

		addTrackToDb(
			album,
			it
		)
	}

	return album
}