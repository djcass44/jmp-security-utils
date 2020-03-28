/*
 *   This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import dev.dcas.gradle.boot
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.springframework.boot") version "2.2.5.RELEASE"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
	id("dev.dcas.gradle-util") version "0.1"
    kotlin("jvm") version "1.3.71"
    kotlin("plugin.spring") version "1.3.71"
    kotlin("kapt") version "1.3.71"
}

group = "dev.dcas.jmp.security"
val projectVersion: String by project
version = projectVersion

dependencies {
	kapt(boot("configuration-processor"))
}

allprojects {
	repositories {
		maven(url = "https://mvn.v2.dcas.dev")
		mavenCentral()
		maven(url = "https://jitpack.io")
	}
	tasks {
		withType<Wrapper> {
			gradleVersion = "6.2.2"
			distributionType = Wrapper.DistributionType.ALL
		}
		withType<KotlinCompile>().all {
			kotlinOptions {
				freeCompilerArgs = listOf("-Xjsr305=strict")
				jvmTarget = "11"
			}
		}
	}
}

subprojects {
	apply(plugin = "org.springframework.boot")
	apply(plugin = "io.spring.dependency-management")
	apply(plugin = "dev.dcas.gradle-util")
	apply(plugin = "org.jetbrains.kotlin.jvm")
	apply(plugin = "org.jetbrains.kotlin.plugin.spring")
	apply(plugin = "maven")
	dependencies {
		implementation(kotlin("stdlib-jdk8"))
		implementation(kotlin("reflect"))

		// spring
		implementation(boot("starter"))
		implementation(boot("starter-web"))
		implementation(boot("starter-security"))
		implementation(boot("configuration-processor"))

		// misc
		implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.10.2")
		implementation("com.github.djcass44:log2:4.1")
		implementation("com.github.djcass44:castive-utilities:v6.RC3") {
			exclude("org.springframework.boot", "spring-boot-starter-data-jpa")
		}

		// testing
		val junitVersion = "5.2.0"
		testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
		testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
		testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")

		testImplementation("org.springframework.boot:spring-boot-starter-test") {
			exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
		}
		testImplementation("org.jetbrains.kotlin:kotlin-test")

		testImplementation("org.hamcrest:hamcrest:2.2")
		testImplementation("org.mockito:mockito-core:3.2.4")
	}

	configure<JavaPluginConvention> {
		sourceCompatibility = JavaVersion.VERSION_11
		targetCompatibility = JavaVersion.VERSION_11
	}
	tasks {
		withType<BootJar> {
			enabled = false
		}
		withType<Test> {
			useJUnitPlatform()
		}
		withType<Jar> {
			enabled = true
		}
	}
}