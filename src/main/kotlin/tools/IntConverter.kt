package org.example.tools

/**
 * Конвертирует минуты в секунды
 */
fun Int.minutes(): Int {
	return this * 60
}

/**
 * Конвертирует часы в секнуды
 */
fun Int.hours(): Int {
	return this.minutes() * 60
}