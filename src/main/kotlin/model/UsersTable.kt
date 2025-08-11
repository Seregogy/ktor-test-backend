package org.example.model

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.UUID

object UsersTable : UUIDTable("USERS") {
	val name = varchar("name", 32)
	var email = varchar("email", 256)
	val about = varchar("about", 512).nullable()
	val avatarLink = text("avatarLink").nullable()
	val passwordHash = text("password_hash")

	val refreshToken = text("refresh_token")
}

object PlaylistsTable : UUIDTable("PLAYLISTS") {
	val owner = reference("owner", UsersTable)
	val name = varchar("name", 32)
	val imageUlr = text("image_url")
	val primaryColor = integer("primary_color")
}

object UsersFavoriteTracksTable : UUIDTable("USERS_FAVORITE_TRACKS_TABLE") {
	val user = reference("user", UsersTable)
	val track = reference("track", TracksTable)
}

class UserEntity(id: EntityID<UUID>) : UUIDEntity(id) {
	companion object : UUIDEntityClass<UserEntity>(UsersTable)

	var name by UsersTable.name
	var email by UsersTable.email
	var about by UsersTable.about
	var avatarLink by UsersTable.avatarLink
	var passwordHash by UsersTable.passwordHash

	var refreshToken by UsersTable.refreshToken

	val playlists by PlaylistEntity referrersOn PlaylistsTable.owner
	var favoriteTracks by TrackEntity via UsersFavoriteTracksTable
}

class PlaylistEntity(id: EntityID<UUID>) : UUIDEntity(id) {
	companion object : UUIDEntityClass<PlaylistEntity>(PlaylistsTable)

	var owner by UserEntity referencedOn PlaylistsTable.owner

	var name by PlaylistsTable.name
	var imageUrl by PlaylistsTable.imageUlr
	var primaryColor by PlaylistsTable.primaryColor
}