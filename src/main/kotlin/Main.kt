package org.example

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respond
import kotlinx.serialization.json.Json
import org.example.model.AlbumEntity
import org.example.model.AlbumsTable
import org.example.model.ArtistAlbumsTable
import org.example.model.ArtistEntity
import org.example.model.ArtistsOnTrackTable
import org.example.model.ArtistsTable
import org.example.model.DirectorEntity
import org.example.model.DirectorsTable
import org.example.model.FilmEntity
import org.example.model.FilmsTable
import org.example.model.ImagesUrlTable
import org.example.model.SocialMediasTable
import org.example.model.TrackEntity
import org.example.model.TrackGenreEntity
import org.example.model.GenresTable
import org.example.model.TracksTable
import org.example.model.TracksToGenreTable
import org.example.routes.audience
import org.example.routes.issuer
import org.example.routes.secret
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.transactions.transaction
import java.awt.Color
import java.util.Date

fun main() {
	connectToDatabase()
	transactionTest()

	/*embeddedServer(
		factory = Netty,
		port = 8080,
		host = "localhost"
	) {
		configure()

		authorizationRoutes()

		userRoutes()
		trackRoutes()

		transactionTest()
		initPostMalone()
	}.start(true)*/
}

fun Application.configure() {
	install(ContentNegotiation) {
		json(
			Json {
				prettyPrint = true
			}
		)
	}

	install(Authentication) {
		jwt("auth-jwt") {
			realm = System.getenv("realm")

			verifier(JWT
				.require(Algorithm.HMAC256(secret))
				.withAudience(audience)
				.withIssuer(issuer)
				.build())

			validate {
				if (it.payload.getClaim("login").asString() != "") {
					JWTPrincipal(it.payload)
				} else {
					null
				}
			}

			challenge { defaultScheme, realm ->
				call.respond(
					HttpStatusCode.Unauthorized,
					"$realm is not valid or expired"
				)
			}
		}
	}

	connectToDatabase()

	transaction {
		SchemaUtils.createMissingTablesAndColumns(
			ArtistsTable, AlbumsTable, TracksTable, GenresTable
		)

		SchemaUtils.createMissingTablesAndColumns(
			ImagesUrlTable, ArtistAlbumsTable, ArtistsOnTrackTable, SocialMediasTable, TracksToGenreTable
		)
	}
}

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

private fun searchArtist(artistName: String) {
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

private fun searchFilm(filmName: String) {
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

private fun initPostMalone() {
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

private fun connectToDatabase() {
	Database.connect("jdbc:sqlite:src/database/database.db", "org.sqlite.JDBC")
}