pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "vipexam"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":app")

include(":core:data")
include(":core:database")
include(":core:network")
include(":core:ui")
include(":core:domain")

include(":feature:wordlist")
include(":feature:settings")
include(":feature:history")
include(":feature:bookmarks")
include(":core:template")
include(":core:preference")
