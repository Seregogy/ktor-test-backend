package org.example.routes.artists

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.example.dto.FullArtist
import org.example.dto.toFullDTO
import org.example.model.ArtistEntity
import org.example.tools.cacheControl
import org.example.tools.minutes
import org.example.tools.tryParseUUIDFromString
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class GetArtistResponse(
	val artist: FullArtist = FullArtist()
)


fun Route.getArtist() {
	get("{id}") {
		val artistId = call.parameters["id"]?.let {
			tryParseUUIDFromString(it)
		} ?: return@get call.respond(
			status = HttpStatusCode.BadRequest,
			message = mapOf(
				"id" to "not stated"
			)
		)

		val artist = transaction {
			ArtistEntity.findById(artistId)
		} ?: return@get call.respond(
			status = HttpStatusCode.NotFound,
			message = mapOf(
				"id" to "invalid"
			)
		)

		call.cacheControl(30.minutes())
		call.respond(GetArtistResponse(artist.toFullDTO()))
	}
}