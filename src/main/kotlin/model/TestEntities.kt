package org.example.model

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.UUID

object FilmsTable : UUIDTable() {
	val name = text("name")
	val about = text("about")
	val minutesDuration = float("minutes_duration")
	val director = reference("director", DirectorsTable)
}

object DirectorsTable : UUIDTable() {
	val name = text("name")
}

class FilmEntity(id: EntityID<UUID>) : UUIDEntity(id) {
	companion object : UUIDEntityClass<FilmEntity>(FilmsTable)

	var name by FilmsTable.name
	var about by FilmsTable.about
	var minutesDuration by FilmsTable.minutesDuration

	var director by DirectorEntity referencedOn FilmsTable.director

	override fun toString(): String {
		return "FilmEntity(name=$name, about=$about, minutesDuration=$minutesDuration)"
	}
}

class DirectorEntity(id: EntityID<UUID>) : UUIDEntity(id) {
	companion object : UUIDEntityClass<DirectorEntity>(DirectorsTable)

	var name by DirectorsTable.name
	val films by FilmEntity referrersOn FilmsTable.director
}