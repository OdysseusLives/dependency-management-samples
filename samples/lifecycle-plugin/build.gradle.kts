import org.gradle.plugins.lifecycle.LIFECYCLE_ATTRIBUTE
import org.gradle.plugins.lifecycle.Lifecycle
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

plugins {
    `build-scan`
    `java-library`
    id("org.gradle.plugins.lifecycle")
}

buildScan {
    setTermsOfServiceUrl("https://gradle.com/terms-of-service")
    setTermsOfServiceAgree("yes")
    publishAlways()
}

dependencies {
    implementation("com.acme:testA:1")
    if (project.hasProperty("deprecated")) {
        implementation("com.acme:testB:+") {
            attributes {
                attribute(LIFECYCLE_ATTRIBUTE, objects.named(Lifecycle::class.java, "DEPRECATED"))
            }
        }
    } else {
        implementation("com.acme:testB:+")
    }
}

// Below is just helpers for the sake of the demo
tasks {
    "insight"(DependencyInsightReportTask::class) {
        configuration = configurations.compileClasspath
        setDependencySpec("test")

        // Make sure we report any exception when resolving
        doFirst {
            configuration.resolve()
        }
    }
}

defaultTasks("insight")

apply {
    from("repositories.gradle.kts")
    from("list-versions.gradle.kts")
}
