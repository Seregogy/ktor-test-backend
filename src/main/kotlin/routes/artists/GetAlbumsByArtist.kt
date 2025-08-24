package org.example.routes.artists

import io.ktor.http.CacheControl
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.CachingOptions
import io.ktor.server.application.call
import io.ktor.server.plugins.cachingheaders.CachingHeaders
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.options
import kotlinx.serialization.Serializable
import org.example.dto.BaseAlbum
import org.example.dto.toBaseDTO
import org.example.model.ArtistEntity
import org.example.tools.hours
import org.example.tools.tryParseUUIDFromString
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class GetAlbumsByArtistResponse(
	val albums: List<BaseAlbum> = listOf()
)

fun Route.getAlbumsFromArtist() {
	get("{id}/albums") {
		install(CachingHeaders) {
			options { CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 1.hours())) }
		}

		val artistId = call.parameters["id"]?.let {
			tryParseUUIDFromString(it)
		} ?: return@get call.respond(
			status = HttpStatusCode.BadRequest,
			message = mapOf(
				"id" to "not stated or invalid"
			)
		)

		val artist = transaction {
			ArtistEntity.findById(artistId)
		} ?: return@get call.respond(
			status = HttpStatusCode.BadRequest,
			message = mapOf(
				"id" to "invalid"
			)
		)

		call.respond(GetAlbumsByArtistResponse(
			albums = transaction { artist.albums.map { it.toBaseDTO() } }
		))
	}
}