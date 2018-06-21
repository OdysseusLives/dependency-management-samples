allprojects {
    group = "org.example"
    version = "1.2"

    repositories {
        maven {
            setUrl("../repo")
        }
        mavenCentral()
    }
}
