import dev.dcas.gradle.boot

plugins {
	kotlin("plugin.jpa") version "1.3.72"
}

group = "dev.dcas.jmp.security"
val projectVersion: String by project
version = projectVersion

extra["springCloudVersion"] = "Hoxton.SR4"

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
	}
}

dependencies {
	// spring
	implementation(boot("starter-data-jpa"))

	implementation(project(":core"))
}