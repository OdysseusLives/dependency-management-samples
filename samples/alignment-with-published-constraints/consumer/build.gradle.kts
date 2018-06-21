plugins {
    java
}

group = "org.example"
version = "2.0"

repositories {
    maven {
        setUrl("../repo")
    }
}

dependencies {
    implementation("org.example:leaf-baz:1.1")
    implementation("org.example:leaf-bar:2.0")
}