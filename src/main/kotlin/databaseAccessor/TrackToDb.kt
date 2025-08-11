package org.example.databaseAccessor

import org.example.model.AlbumEntity
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.pathString

fun addTrackToDb(
	albumEntity: AlbumEntity,
	trackPath: Path
) {
	println("\t\t${trackPath}, ${trackPath.exists()}")
}