package org.example.routes.tracks

import io.ktor.http.*
import io.ktor.http.content.CachingOptions
import io.ktor.server.plugins.cachingheaders.CachingHeaders
import io.ktor.server.plugins.cachingheaders.caching
import io.ktor.server.plugins.origin
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.dto.toFullDTO
import org.example.model.TrackEntity
import org.example.routes.auth.externalPort
import org.example.routes.auth.externalHost
import org.example.tools.cacheControl
import org.example.tools.hours
import org.example.tools.minutes
import org.example.tools.tryParseUUIDFromString
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.time.Duration.Companion.minutes

fun Route.getTrack() {
	get("{id}") {
		val trackId = call.parameters["id"]?.let {
			tryParseUUIDFromString(it)
		}?: return@get call.respond(
			status = HttpStatusCode.BadRequest,
			message = mapOf(
				"error" to "id not stated or invalid"
			)
		)

		val track = transaction {
			TrackEntity.findById(trackId)
		} ?: return@get call.respond(
			status = HttpStatusCode.NotFound,
			message = mapOf(
				"error" to "id invalid"
			)
		)

		transaction { track.listening += 1 }

		call.cacheControl(10.minutes())
		call.respond(
			track.toFullDTO(call.request.origin.let {
				"https://${externalHost}/audio/${track.id}.mp3"
			})
		)
	}
}