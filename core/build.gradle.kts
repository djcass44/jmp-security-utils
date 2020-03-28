import dev.dcas.gradle.boot
import dev.dcas.gradle.cloud

group = "dev.dcas.jmp.security"
val projectVersion: String by project
version = projectVersion


extra["springCloudVersion"] = "Hoxton.SR3"

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
	}
}

dependencies {
	implementation("io.jsonwebtoken:jjwt:0.7.0")
	implementation("com.github.scribejava:scribejava-apis:6.9.0")

	// spring
	implementation(boot("starter-data-ldap"))
	implementation(cloud("starter-openfeign"))
}