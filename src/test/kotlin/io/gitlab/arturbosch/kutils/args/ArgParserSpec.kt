package io.gitlab.arturbosch.kutils.args

import io.kotlintest.matchers.plusOrMinus
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

/**
 * @author Artur Bosch
 */
internal class ArgParserSpec : StringSpec({

    val parser = ArgParser.create("kutils", "kotlin utilities") {
        addBoolOption("bool", false, 'b', help = "Is bool?")
        addIntOption("int", false, 'i', help = "Magic int number")
        addLongOption("long", false, 'l', help = "Magic long number")
        addDoubleOption("double", false, 'd', help = "Magic double number - no short")
        addStringOption("string", false, 's', help = "Magic string")
    }

    "parse all arguments with short form" {
        parser.parse(arrayOf("-b", "true", "-i", "5", "-l", "7", "-d", "5.5", "-s", "magic"))

        parser.boolValue("bool") shouldBe true
        parser.boolValue('b') shouldBe true
        parser.intValue('i') shouldBe 5
        parser.intValue("int") shouldBe 5
        parser.longValue("long") shouldBe 7L
        parser.longValue('l') shouldBe 7L
        parser.doubleValue("double") shouldBe (5.5 plusOrMinus 0.1)
        parser.doubleValue('d') shouldBe (5.5 plusOrMinus 0.1)
        parser.stringValue("string") shouldBe "magic"
        parser.stringValue('s') shouldBe "magic"
    }

    "parse all arguments with long form" {
        parser.parse(arrayOf("--bool", "true", "--int", "5", "--long", "7", "--double", "5.5", "--string", "magic"))

        parser.boolValue("bool") shouldBe true
        parser.boolValue('b') shouldBe true
        parser.intValue("int") shouldBe 5
        parser.intValue('i') shouldBe 5
        parser.longValue("long") shouldBe 7L
        parser.longValue('l') shouldBe 7L
        parser.doubleValue("double") shouldBe (5.5 plusOrMinus 0.1)
        parser.doubleValue('d') shouldBe (5.5 plusOrMinus 0.1)
        parser.stringValue("string") shouldBe "magic"
        parser.stringValue('s') shouldBe "magic"
    }

    "parse all arguments with long form and equals sign" {
        parser.parse(arrayOf("--bool=true", "--int=5", "--long=7", "--double=5.5", "--string=magic"))

        parser.boolValue("bool") shouldBe true
        parser.boolValue('b') shouldBe true
        parser.intValue("int") shouldBe 5
        parser.intValue('i') shouldBe 5
        parser.longValue("long") shouldBe 7L
        parser.longValue('l') shouldBe 7L
        parser.doubleValue("double") shouldBe (5.5 plusOrMinus 0.1)
        parser.doubleValue('d') shouldBe (5.5 plusOrMinus 0.1)
        parser.stringValue("string") shouldBe "magic"
        parser.stringValue('s') shouldBe "magic"
    }

    "parse all arguments with short form and equals sign" {
        parser.parse(arrayOf("-b", "-i=5", "-l=7", "-d=5.5", "-s=magic"))

        parser.boolValue("bool") shouldBe true
        parser.boolValue('b') shouldBe true
        parser.intValue("int") shouldBe 5
        parser.intValue('i') shouldBe 5
        parser.longValue("long") shouldBe 7L
        parser.longValue('l') shouldBe 7L
        parser.doubleValue("double") shouldBe (5.5 plusOrMinus 0.1)
        parser.doubleValue('d') shouldBe (5.5 plusOrMinus 0.1)
        parser.stringValue("string") shouldBe "magic"
        parser.stringValue('s') shouldBe "magic"
    }

    "parse all arguments with mixed forms" {
        parser.parse(arrayOf("--bool", "--int=5", "-l", "7", "--double", "5.5", "-s", "magic"))

        parser.boolValue("bool") shouldBe true
        parser.boolValue('b') shouldBe true
        parser.intValue("int") shouldBe 5
        parser.intValue('i') shouldBe 5
        parser.longValue("long") shouldBe 7L
        parser.longValue('l') shouldBe 7L
        parser.doubleValue("double") shouldBe (5.5 plusOrMinus 0.1)
        parser.doubleValue('d') shouldBe (5.5 plusOrMinus 0.1)
        parser.stringValue("string") shouldBe "magic"
        parser.stringValue('s') shouldBe "magic"
    }

    "no arguments passed" {
        parser.parse(arrayOf())

        parser.boolValue("bool") shouldBe null
        parser.boolValue('b') shouldBe null
        parser.intValue('i') shouldBe null
        parser.intValue("int") shouldBe null
        parser.longValue("long") shouldBe null
        parser.longValue('l') shouldBe null
        parser.doubleValue("double") shouldBe null
        parser.doubleValue('d') shouldBe null
        parser.stringValue("string") shouldBe null
        parser.stringValue('s') shouldBe null
    }

    "no arguments passed, defaults returned" {
        parser.parse(arrayOf())

        parser.boolValue("bool", false) shouldBe false
        parser.boolValue('b', true) shouldBe true
        parser.intValue('i', 1) shouldBe 1
        parser.intValue("int", 2) shouldBe 2
        parser.longValue("long", 1L) shouldBe 1L
        parser.longValue('l', 2L) shouldBe 2L
        parser.doubleValue("double", 0.0) shouldBe (0.0 plusOrMinus 0.0)
        parser.doubleValue('d', 0.1) shouldBe (0.1 plusOrMinus 0.01)
        parser.stringValue("string", "magic") shouldBe "magic"
        parser.stringValue('s', "panic") shouldBe "panic"
    }

    "can print usage message" {
        val help = parser.getHelp()

        help shouldBe """
Usage: kutils [{--string, -s}=value1] [{--double, -d}=value2] [{--bool, -b}] [{--long, -l}=value3] [{--int, -i}=value4]
Description: kotlin utilities
Options:
	-b, --bool:		Is bool?
	-i, --int:		Magic int number
	-l, --long:		Magic long number
	-d, --double:	Magic double number - no short
	-s, --string:	Magic string
	""".trimIndent().trimEnd()
    }
})
