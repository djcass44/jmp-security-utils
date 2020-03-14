plugins {
	kotlin("plugin.jpa") version "1.3.70"
}

group = "dev.dcas.jmp.security"
val projectVersion: String by project
version = projectVersion

extra["springCloudVersion"] = "Hoxton.SR2"

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
	}
}

dependencies {
	// spring
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")

	implementation(project(":core"))
}