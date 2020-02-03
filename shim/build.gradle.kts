plugins {
	kotlin("plugin.jpa") version "1.3.61"
}

group = "dev.dcas.jmp.security"
val projectVersion: String by project
version = projectVersion

dependencies {
	// spring
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")

	implementation(project(":core"))
}