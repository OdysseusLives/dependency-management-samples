package org.gradle.plugins.lifecycle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

open class LifecyclePlugin @Inject constructor(private val objectFactory: ObjectFactory) : Plugin<Project> {

    override
    fun apply(project: Project): Unit = project.run {
        configureSchema()
        configureDefaultLifecycle()
        configureGraphValidation()
        configureLifecycle()
    }

    private
    fun Project.configureSchema() {
        dependencies.attributesSchema.attribute(LIFECYCLE_ATTRIBUTE) {
            compatibilityRules.add(LifecycleCompatibilityRule::class.java)
            disambiguationRules.add(LifecycleDisambiguationRules::class.java) {
                params(lifecycle(ALIVE))
                params(lifecycle(DEPRECATED))
                params(lifecycle(BLACKLISTED))
            }
        }
    }

    private
    fun Project.configureDefaultLifecycle() {
        configurations.all {
            attributes {
                attribute(LIFECYCLE_ATTRIBUTE, objects.named(Lifecycle::class.java, ALIVE))
            }
        }
    }

    private
    fun Project.configureGraphValidation() {
        configurations.all {
            val configName = name
            incoming.afterResolve {
                resolutionResult.allComponents {
                    findLifecycleAttribute(variant.attributes)?.let {
                        if (it == BLACKLISTED) {
                            throw RuntimeException("Configuration $configName resolved a blacklisted module: $id")
                        }
                        if (it == DEPRECATED) {
                            logger.warn("Configuration $configName resolved a deprecated module: $id")
                        }
                    }
                }
            }
        }
    }

    private fun Project.configureLifecycle() {
        project.dependencies.markAllAlive()

        // this could come from an external service
        project.dependencies.deprecate("com.acme:testB", "3")
        project.dependencies.blacklist("com.acme:testB", "4")
    }

    private fun DependencyHandler.markAllAlive() {
        components.all {
            attributes {
                attribute(LIFECYCLE_ATTRIBUTE, lifecycle(ALIVE))
            }
        }
    }

    private fun DependencyHandler.deprecate(module: String, version: String) {
        components.withModule(module) {
            if (id.version == version) {
                attributes {
                    attribute(LIFECYCLE_ATTRIBUTE, lifecycle(DEPRECATED))
                }
            }
        }
    }

    private fun DependencyHandler.blacklist(module: String, version: String) {
        components.withModule(module) {
            if (id.version == version) {
                attributes {
                    attribute(LIFECYCLE_ATTRIBUTE, lifecycle(BLACKLISTED))
                }
            }
        }
    }

    private
    fun lifecycle(name: String) = objectFactory.named(Lifecycle::class.java, name)

}
