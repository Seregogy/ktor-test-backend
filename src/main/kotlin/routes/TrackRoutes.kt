package org.example.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.partialcontent.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

fun Application.trackRoutes() {
	install(PartialContent)
	install(AutoHeadResponse)

	routing {
		get("ktor") {
			val file = File("src/files/ktor_logo.png")
			call.response.header(
				HttpHeaders.ContentDisposition,
				ContentDisposition.Attachment.withParameter(ContentDisposition.Parameters.FileName, "ktor_logo.png")
					.toString()
			)

			call.respondFile(file)
		}

		get("hello") {
			call.respondText(File("src/files/text.txt").readText())
		}
	}
}