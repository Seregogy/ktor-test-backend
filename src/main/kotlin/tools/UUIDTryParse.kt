package org.example.tools

import java.util.UUID

fun tryParseUUIDFromString(name: String): UUID? {
	return try {
		UUID.fromString(name)
	} catch (_: IllegalArgumentException) {
		null
	}
}