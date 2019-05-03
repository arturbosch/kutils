package io.gitlab.arturbosch.kutils.args

import java.util.LinkedList

@Suppress("UNCHECKED_CAST")
open class ArgParser(val name: String, val desc: String) {

    companion object {
        fun create(name: String, desc: String, init: ArgParser.() -> Unit) =
                ArgParser(name, desc).apply(init)
    }

    private val options = HashMap<String, Option<*>>(10)
    private val required = HashMap<String, Option<*>>(10)
    private var values = HashMap<String, Option<*>>(10)
    private var help = LinkedList<String>()

    private fun <T> addOption(option: Option<T>): ArgParser {
        if (option.shortForm != null) {
            options["-" + option.shortForm] = option
        }

        if (!options.containsKey(option.longForm)) {
            val helpDesc = option.getHelp()
            if (helpDesc != null) {
                help.add(helpDesc)
            }
        }

        if (option.isRequired && !required.containsKey(option.longForm)) {
            required[option.longForm] = option
        }

        options["--" + option.longForm] = option

        return this
    }

    @Throws(IllegalOptionValueException::class)
    private fun <T> addValue(option: Option<T>, valueArg: String) {
        val longForm = option.longForm

        option.setValue(valueArg)

        if (values.containsKey(longForm)) {
            values.remove(longForm)
        }

        values[longForm] = option
    }

    private fun <T> getValue(option: Option<T>, default: T? = null): T? {
        val found = values[option.longForm]
        return if (found == null) default
        else found.getValue() as T ?: default
    }

    private fun <T> getValue(shortName: Char, default: T? = null): T? {
        val option = options["-" + shortName.toString()] as? Option<T>
        return if (option == null) null else getValue(option, default)
    }

    private fun <T> getValue(longName: String, default: T? = null): T? {
        val option = options["--$longName"] as? Option<T>
        return if (option == null) null else getValue(option, default)
    }

    fun addStringOption(
        longForm: String,
        isRequired: Boolean = false,
        shortForm: Char? = null,
        help: String? = null
    ): ArgParser = addOption(StringOption(longForm, isRequired, shortForm, help))

    fun addIntOption(
        longForm: String,
        isRequired: Boolean = false,
        shortForm: Char? = null,
        help: String? = null
    ): ArgParser = addOption(IntegerOption(longForm, isRequired, shortForm, help))

    fun addLongOption(
        longForm: String,
        isRequired: Boolean = false,
        shortForm: Char? = null,
        help: String? = null
    ): ArgParser = addOption(LongOption(longForm, isRequired, shortForm, help))

    fun addDoubleOption(
        longForm: String,
        isRequired: Boolean = false,
        shortForm: Char? = null,
        help: String? = null
    ): ArgParser = addOption(DoubleOption(longForm, isRequired, shortForm, help))

    fun addBoolOption(
        longForm: String,
        isRequired: Boolean = false,
        shortForm: Char? = null,
        help: String? = null
    ): ArgParser = addOption(BooleanOption(longForm, isRequired, shortForm, help))

    fun stringValue(longForm: String, default: String? = null): String? = getValue(longForm, default)

    fun stringValue(shortForm: Char, default: String? = null): String? = getValue(shortForm, default)

    fun intValue(longForm: String, default: Int? = null): Int? = getValue(longForm, default)

    fun intValue(shortForm: Char, default: Int? = null): Int? = getValue(shortForm, default)

    fun longValue(longForm: String, default: Long? = null): Long? = getValue(longForm, default)

    fun longValue(shortForm: Char, default: Long? = null): Long? = getValue(shortForm, default)

    fun doubleValue(longForm: String, default: Double? = null): Double? = getValue(longForm, default)

    fun doubleValue(shortForm: Char, default: Double? = null): Double? = getValue(shortForm, default)

    fun boolValue(longForm: String, default: Boolean? = null): Boolean? = getValue(longForm, default)

    fun boolValue(shortForm: Char, default: Boolean? = null): Boolean? = getValue(shortForm, default)

    @Throws(UnknownOptionException::class,
            IllegalOptionValueException::class,
            UnknownSubOptionException::class,
            NotFlagException::class,
            RequiredOptionException::class)
    fun parse(args: Array<String>) {
        var position = 0
        values = HashMap(10)

        loop@ while (position < args.size) {
            val arg = args[position]

            if (arg.startsWith("-")) {
                val next = if (position < args.size - 1) args[position + 1] else null
                when {
                    arg == "--" -> break@loop
                    arg.startsWith("--") -> handleLongOption(arg, next)
                    arg.startsWith("-") -> handleShortOption(arg, next)
                    else -> {
                        // skip args with more '-'
                    }
                }
            }

            position++
        }

        for ((_: String, option: Option<*>) in required) {
            val longForm = option.longForm
            if (!values.containsKey(longForm)) {
                throw RequiredOptionException(option)
            }
        }
    }

    private fun splitOnEquals(line: String, next: String?) =
            line.substringBefore("=") to line.substringAfter("=", next ?: "")

    protected open fun handleLongOption(line: String, next: String?) {
        handleOption(line, next)
    }

    protected open fun handleShortOption(line: String, next: String?) {
        handleOption(line, next)
    }

    private fun handleOption(line: String, next: String?) {
        val (key, value) = splitOnEquals(line, next)
        val option = options[key]

        when {
            option == null -> throw UnknownOptionException(key)
            option.withValue -> addValue(option, value)
            else -> addValue(option, "")
        }
    }

    protected open fun getUsage(): String {
        val formats = LinkedList<String>()

        var counter = 1
        for ((key: String, option: Option<*>) in options)
            if (key.startsWith("--")) {
                var format: String

                format =
                        if (option.shortForm == null) "--" + option.longForm + ""
                        else "{--" + option.longForm + ", -" + option.shortForm + "}"

                if (option.withValue) format += "=value" + counter++

                if (!option.isRequired) format = "[$format]"

                formats.add(format)
            }

        return "Usage: ${this.name} " + if (formats.size > 0) formats.joinToString(" ") else ""
    }

    protected open fun getDescription(): String {
        return "Description: ${this.desc}"
    }

    protected open fun getOptionsDescription(): String {
        return if (help.size > 0) "Options:" + "\n\t" + help.joinToString("\n\t") else ""
    }

    open fun getHelp(): String {
        return getUsage() + "\n" +
                getDescription() + "\n" +
                getOptionsDescription()
    }
}
