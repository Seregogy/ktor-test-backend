package org.example.routes.tracks

import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.*
import org.example.model.TracksTable
import org.jetbrains.exposed.sql.Random
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.getRandomTrackId() {
	get("random/id") {
		val id = transaction { TracksTable.selectAll().orderBy(Random()).first()[TracksTable.id] }

		call.respond(mapOf("id" to id.value.toString()))
	}
}