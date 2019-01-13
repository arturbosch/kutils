package io.gitlab.arturbosch.kutils.args

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

internal class ArgsTest : StringSpec({

    "get help=false by default" {
        val parser = ArgParser.create("kutils", "") {
            addBoolOption("help", true)
        }
        Args(parser).help shouldBe false
    }

    "get help=true by parsing" {
        val parser = ArgParser.create("kutils", "") {
            addBoolOption("help", true)
        }
        parser.parse(arrayOf("--help"))
        val help = Args(parser).help
        help shouldBe true
    }

    "get options by delegation" {
        val parser = ArgParser.create("kutils", "") {
            addBoolOption("help")
            addBoolOption("bool")
            addIntOption("int")
            addStringOption("str")
        }

        parser.parse(arrayOf("--int", "5", "--str", "hello"))
        val args = MyArgs(parser)

        args.help shouldBe false
        args.i shouldBe 5
        args.s shouldBe "hello"
        args.b shouldBe null
    }
})

class MyArgs(parser: ArgParser) : Args(parser) {

    val i by parser.get<Int>("int")
    val s: String? by parser.getOrNull("str")
    val b by parser.getOrNull<Boolean>("bool")
}
