plugins {
    `java-library`
    `maven-publish`
}

publishing {
    repositories {
        maven(url = uri("${rootProject.projectDir}/../repo"))
    }
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}