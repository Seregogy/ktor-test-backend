package org.example.routes.tracks

import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.tracksRoutes() {
	routing {
		route("/api/v1/tracks") {
			getRandomTrack()
			getTrack()
			toggleLike()
		}
	}
}