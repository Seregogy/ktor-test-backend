package org.example

import org.example.model.ArtistEntity
import org.example.model.ArtistImageEntity
import org.example.model.ArtistsTable
import org.example.model.TrackEntity
import org.example.model.TracksTable
import org.jetbrains.exposed.sql.transactions.transaction

fun main() {
	connectToDatabase()
	/*transaction {
		ArtistEntity.find {
			ArtistsTable.name eq "Markul"
		}.firstOrNull()?.let { artistEntity ->
			ArtistImageEntity.new {
				artist = artistEntity
				imageUrl = "https://img08.rl0.ru/afisha/e1200x1200i/daily.afisha.ru/uploads/images/1/c6/1c662dc779cd12974a28eadce7a01dd0.jpg"
			}

			ArtistImageEntity.new {
				artist = artistEntity
				imageUrl = "https://www.soyuz.ru/public/uploads/files/2/7647312/2025080414071643aa684bad.jpg"
			}
		}
	}*/
	//addArtistToDb(Path("src/files/Markul"))
}