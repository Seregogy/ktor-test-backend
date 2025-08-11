package org.example.routes

import io.ktor.server.application.Application
import io.ktor.server.http.content.staticFiles
import io.ktor.server.routing.routing
import java.io.File

fun Application.servingFilesRoutes() {
	routing {
		staticFiles("/resources", File("src/files/images"))
	}
}