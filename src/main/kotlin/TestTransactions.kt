package org.example

import org.example.model.AlbumEntity
import org.example.model.ArtistEntity
import org.example.model.ArtistsTable
import org.example.model.DirectorEntity
import org.example.model.DirectorsTable
import org.example.model.FilmEntity
import org.example.model.FilmsTable
import org.example.model.TrackEntity
import org.example.model.TrackGenreEntity
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.transactions.transaction
import java.awt.Color
import java.util.Date

fun transactionTest() {
	transaction {
		val postMalone = ArtistEntity.find {
			ArtistsTable.name eq "Post Malone"
		}.first()

		println(postMalone.name)

		postMalone.albums.forEach { album ->
			println("Album: ${album.name}")
			album.tracks.forEach { track ->
				println("\tTrack: ${track.name}")
				track.genres.forEach { genre ->
					println("\t\t${genre.name}")
				}
			}
		}
	}
}

fun searchArtist(artistName: String) {
	ArtistEntity.find {
		ArtistsTable.name eq artistName
	}.firstOrNull()?.let { artist ->
		println("Артист: ${artist.name}")
		println("\t${artist.about}")
		println("\n\tПрослушиваний: ${artist.listeningInMonth}")
		println("\tЛайков: ${artist.likes}")

		println("Изображения:")
		artist.imagesUrl.forEach { image ->
			println("\tUrl: ${image.imageUrl}")
			println("\tЦвет: ${Color(image.primaryColor).rgb}")
		}
	}
}

fun searchFilm(filmName: String) {
	FilmEntity.find {
		FilmsTable.name eq filmName
	}.firstOrNull()?.let { film ->
		println("Фильм: ${film.name}")
		println("Режиссёр: ${film.director.name}")

		println("Другие фильмы от ${film.director.name}:")
		DirectorEntity.find {
			DirectorsTable.id eq film.director.id
		}.firstOrNull()?.films?.forEach { directorsFilm ->
			println("\t${directorsFilm.name}")
		}
	}
}

fun initPostMalone() {
	transaction {
		val hipHop = TrackGenreEntity.new {
			name = "Hip Hop"
		}

		val postMalone = ArtistEntity.new {
			name = "Post Malone"
			about = "american rapper"
			likes = 1053562
			listeningInMonth = 1467562
		}

		val lukeCombs = ArtistEntity.new {
			name = "Luke Combs"
			about = "Country singer"
			likes = 5234
			listeningInMonth = 31414
		}

		val f1Trillion = AlbumEntity.new {
			name = "F-1 Trillion: Long Bed"
			likes = 1428
			listening = 9000000
			label = "Post Malone"
			primaryColor = 0x5A858C
			imageUrl = "https://i.scdn.co/image/ab67616d0000b27388208159b1b3c69eefdeb2e0"
			releaseDate = Date(2024).time

			artists = SizedCollection(postMalone)
		}

		val iHadSomeHelp = TrackEntity.new {
			name = "I Had Some Help"
			listening = 1000000
			artists = SizedCollection(postMalone)
			indexInAlbum = 1
			isExplicit = true
			durationSeconds = 188
			lyrics = "not stated"
			album = f1Trillion

			genres = SizedCollection(hipHop)
		}

		val guyForThat = TrackEntity.new {
			name = "Guy For That"
			listening = 1000000
			artists = SizedCollection(postMalone, lukeCombs)
			indexInAlbum = 2
			isExplicit = true
			durationSeconds = 164
			lyrics = "not stated"
			album = f1Trillion

			genres = SizedCollection(hipHop)
		}
	}
}