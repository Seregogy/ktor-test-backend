plugins {
	kotlin("jvm") version "2.1.21"
	kotlin("plugin.serialization") version "2.1.21"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
	mavenCentral()
}

val ktorVersion = "2.3.7"
val exposedVersion = "0.51.0"

dependencies {
	testImplementation(kotlin("test"))
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")

	implementation("io.ktor:ktor-server-core:${ktorVersion}")
	implementation("io.ktor:ktor-server-netty:${ktorVersion}")
	implementation("io.ktor:ktor-server-swagger:${ktorVersion}")

	implementation("io.ktor:ktor-server-auth:${ktorVersion}")
	implementation("io.ktor:ktor-server-auth-jwt:${ktorVersion}")

	implementation("io.ktor:ktor-serialization-kotlinx-json:${ktorVersion}")
	implementation("io.ktor:ktor-server-content-negotiation:${ktorVersion}")
	implementation("io.ktor:ktor-server-partial-content:${ktorVersion}")
	implementation("io.ktor:ktor-server-auto-head-response:${ktorVersion}")

	implementation("io.ktor:ktor-server-html-builder:${ktorVersion}")

	implementation("ch.qos.logback:logback-classic:1.5.18")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

	implementation("org.jetbrains.exposed:exposed-core:${exposedVersion}")
	implementation("org.jetbrains.exposed:exposed-dao:${exposedVersion}")
	implementation("org.jetbrains.exposed:exposed-jdbc:${exposedVersion}")

	implementation("org.xerial:sqlite-jdbc:3.49.1.0")
	implementation("com.h2database:h2:2.2.224")
}

tasks.test {
	useJUnitPlatform()
}
kotlin {
	jvmToolchain(22)
}