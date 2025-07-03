plugins {
	kotlin("jvm") version "2.1.21"
	kotlin("plugin.serialization") version "2.1.21"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
	mavenCentral()
}

dependencies {
	testImplementation(kotlin("test"))
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")

	implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.7")
	implementation("io.ktor:ktor-server-content-negotiation:2.3.7")
	implementation("io.ktor:ktor-server-core:2.3.7")
	implementation("io.ktor:ktor-server-netty:2.3.7")
	implementation("ch.qos.logback:logback-classic:2.3.7")
	implementation("io.ktor:ktor-server-html-builder:2.3.7")
	implementation("ch.qos.logback:logback-classic:1.5.13")

	implementation("org.jetbrains.exposed:exposed-core:0.41.1")
	implementation("org.jetbrains.exposed:exposed-dao:0.41.1")
	implementation("org.jetbrains.exposed:exposed-jdbc:0.41.1")
	implementation("org.xerial:sqlite-jdbc:3.42.0.0")
	implementation("com.h2database:h2:2.2.224")
}

tasks.test {
	useJUnitPlatform()
}
kotlin {
	jvmToolchain(22)
}