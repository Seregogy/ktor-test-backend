package org.example

import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.example.routes.userRoutes

fun main() {
	embeddedServer(
		factory = Netty,
		port = 8080,
		host = "localhost"
	) {
		userRoutes()
	}.start(true)
}