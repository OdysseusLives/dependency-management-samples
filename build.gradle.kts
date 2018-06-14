plugins {
    `java-library`
    `groovy`
}

repositories {
    maven {
        url = uri("https://repo.gradle.org/gradle/ext-releases-local")
    }
    maven {
        url = uri("https://repo.gradle.org/gradle/libs-releases")
    }
    jcenter()
}

dependencies {
    testImplementation("org.gradle:sample-check:0.1.0")
    testImplementation(localGroovy())
    testImplementation("junit:junit:4.12")
}
