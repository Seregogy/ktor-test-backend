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
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.partialcontent.*
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.example.databaseAccessor.addArtistToDb
import org.example.model.*
import org.example.routes.albums.albumsRoutes
import org.example.routes.artists.artistsRoutes
import org.example.routes.auth.audience
import org.example.routes.auth.authRoutes
import org.example.routes.auth.issuer
import org.example.routes.auth.secret
import org.example.routes.servingFilesRoutes
import org.example.routes.tracks.tracksRoutes
import org.example.routes.userRoutes
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.nio.file.Path

fun main() {
	prepareToServerLaunch()

	embeddedServer(
		factory = Netty,
		port = 8080,
		host = "0.0.0.0"
	) {
		configure()

		servingFilesRoutes()
		authRoutes()
		userRoutes()
		tracksRoutes()
		albumsRoutes()
		artistsRoutes()
	}.start(true)
}

fun Application.configure() {
	install(CORS)

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

	routing { swaggerUI(path = "docs", swaggerFile = "src/docs/documentation.yaml") }
}

private fun connectToDatabase() {
	val dbPath = "src/files/database.db"
	Database.connect("jdbc:sqlite:$dbPath", "org.sqlite.JDBC")
}

private fun prepareToServerLaunch() {
	System.setProperty("io.netty.transport.noNative", "true")
	System.setProperty("io.netty.noPreferDirect", "true")
}