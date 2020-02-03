group = "dev.dcas.jmp.security"
val projectVersion: String by project
version = projectVersion

dependencies {
	implementation("io.jsonwebtoken:jjwt:0.7.0")
	implementation("com.github.scribejava:scribejava-apis:6.8.1")
}