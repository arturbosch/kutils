package io.gitlab.arturbosch.kutils

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

internal class ApplicationHomeFolderTest : StringSpec({

    val home = object : ApplicationHomeFolder(resourceAsPath("baseDir")) {}

    "adding properties from file" {
        home.addPropertiesFromFile(resourceAsPath("baseDir/test.properties"))

        home.property("key").shouldBe("value")
        home.property("key2").shouldBe("value")
        home.property("bla").shouldBe(null)
    }

    "adding additional properties from string" {
        home.addPropertiesFromSeparatedString("test=test.test2=test", '.')

        home.property("test").shouldBe("test")
        home.property("test2").shouldBe("test")
        home.property("test3").shouldBe(null)
    }

    "adding properties from a map" {
        home.addProperties(mapOf("map" to "yes"))

        home.property("map").shouldBe("yes")
    }

    "adding property pair" {
        home.addProperty("pair", "yes")

        home.property("pair").shouldBe("yes")
    }
})
