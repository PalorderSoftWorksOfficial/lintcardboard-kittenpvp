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
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

rootProject.name = "lintcardboard"

include(
    "common",
    "platform-spi",
    "mixin-support",
    "bootstrap-agent",
    "server-brand",
    "plugin",
    "live-tests",
)
