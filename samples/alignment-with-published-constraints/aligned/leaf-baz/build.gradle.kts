plugins {
    `java-library`
    `maven-publish`
}

dependencies {
    api(project(":base-foo"))
    constraints {
        add("api", "org.example:leaf-bar:$version")
    }
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