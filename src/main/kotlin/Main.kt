package org.example

import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main() {
	embeddedServer(
		factory = Netty,
		port = 8080,
		host = "localhost"
	) {
		configureRouting()
	}.start(wait = true)
}