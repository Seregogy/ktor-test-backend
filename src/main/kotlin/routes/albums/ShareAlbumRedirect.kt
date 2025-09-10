package org.example.routes.albums

import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

//TODO: требует тестирования на клиенте
fun Route.shareAlbumRedirect() {
	get("{id}/share") {
		val albumId = call.parameters["id"] ?: return@get call.respond(
			status = HttpStatusCode.BadRequest,
			message = mapOf(
				"id" to "not stated"
			)
		)

		call.respondRedirect("musicapp://seregogy/album-page/$albumId")
	}
}