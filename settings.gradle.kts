pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
    }
}

rootProject.name = "lintcardboard"

include(
    "common",
    "platform-spi",
    "mixin-support",
    "bootstrap-agent",
    "live-tests",
)
