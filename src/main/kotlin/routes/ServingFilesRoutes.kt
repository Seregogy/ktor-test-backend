package org.example.routes

import io.ktor.http.ContentType
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import java.io.File

fun Application.servingFilesRoutes() {
	routing {
		staticFiles("/images", File("src/files/images"))

		staticFiles("/audio", dir = File("src/files/audio")) {
			contentType {
				ContentType.Audio.MPEG
			}
		}
	}
}