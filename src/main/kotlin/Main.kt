package org.example

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.autohead.AutoHeadResponse
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.partialcontent.PartialContent
import io.ktor.server.response.*
import kotlinx.serialization.json.Json
import org.example.model.*
import org.example.routes.albums.albumsRoutes
import org.example.routes.auth.audience
import org.example.routes.auth.authRoutes
import org.example.routes.auth.issuer
import org.example.routes.auth.secret
import org.example.routes.servingFilesRoutes
import org.example.routes.tracks.tracksRoutes
import org.example.routes.userRoutes
import org.jetbrains.exposed.dao.load
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.transactions.transaction

fun main() {
	embeddedServer(
		factory = Netty,
		port = 8080,
		host = "192.168.1.64"
	) {
		configure()

		servingFilesRoutes()
		authRoutes()
		userRoutes()
		tracksRoutes()
		albumsRoutes()
	}.start(true)
}

fun Application.configure() {
	install(ContentNegotiation) {
		json(
			Json {
				prettyPrint = true
			}
		)
	}

	install(AutoHeadResponse)

	install(PartialContent) { }

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
			ArtistsTable, AlbumsTable, TracksTable, GenresTable, UsersTable, PlaylistsTable
		)

		SchemaUtils.createMissingTablesAndColumns(
			ImagesUrlTable, ArtistAlbumsTable, ArtistsOnTrackTable, SocialMediasTable, TracksToGenreTable,
			UsersFavoriteTracksTable
		)
	}
}

fun connectToDatabase() {
	Database.connect("jdbc:sqlite:src/database/database.db", "org.sqlite.JDBC")
}