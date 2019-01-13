package io.gitlab.arturbosch.kutils

import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec

internal class ApplicationHomeFolderTest : BehaviorSpec({

    given("application home") {
        val home = object : ApplicationHomeFolder(resourceAsPath("baseDir")) {}

        `when`("adding properties from file") {
            home.addPropertiesFromFile(resourceAsPath("baseDir/test.properties"))

            then("key and key2 should be present") {
                home.property("key").shouldBe("value")
                home.property("key2").shouldBe("value")
                home.property("bla").shouldBe(null)
            }
        }

        `when`("adding additional properties from string") {
            home.addPropertiesFromSeparatedString("test=test.test2=test", '.')

            then("test=test and test2=test should be included") {
                home.property("test").shouldBe("test")
                home.property("test2").shouldBe("test")
                home.property("test3").shouldBe(null)
            }
        }

        `when`("adding properties from a map") {
            home.addProperties(mapOf("map" to "yes"))

            then("map=yes is included") {
                home.property("map").shouldBe("yes")
            }
        }

        `when`("adding property pair") {
            home.addProperty("pair", "yes")

            then("pair=yes is included") {
                home.property("pair").shouldBe("yes")
            }
        }
    }
})
