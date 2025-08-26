package org.example.routes.artists

import io.ktor.server.application.Application
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.artistsRoutes() {
	routing {
		route("api/v1/artists") {
			getArtist()
			getArtistTopTracks()
			getTopArtists()
			getAlbumsFromArtist()
		}
	}
}