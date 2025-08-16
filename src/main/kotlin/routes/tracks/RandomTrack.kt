package org.example.routes.tracks

import io.ktor.server.application.call
import io.ktor.server.plugins.origin
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.example.model.TrackEntity
import org.example.model.TracksTable
import org.jetbrains.exposed.sql.Random
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.getRandomTrack() {
	get("random") {
		val track = transaction {
			TracksTable.selectAll().orderBy(Random()).limit(1).first().let {
				TrackEntity.wrapRow(it)
			}
		}

		call.respondRedirect("/api/v1/tracks/${track.id.value}")
	}
}