package org.example.routes.albums

import io.ktor.server.application.Application
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.albumsRoutes() {
	routing {
		route("/api/v1/albums") {
			getAlbumById()
			shareAlbumRedirect()
		}
	}
}