package org.example.routes.tracks

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class GetTracksRequest(
	val tracks: List<String>
)

fun Route.getTracks() {
	post("tracks") {
		val request = call.receive<GetTracksRequest>()

		runCatching {
			val tracks = transaction {
				request.tracks.map {

				}
			}
		}.onFailure {
			return@post call.respond(
				status = HttpStatusCode.BadRequest,
				message = mapOf(
					"error" to "invalid request"
				)
			)
		}
	}
}