package io.gitlab.arturbosch.kutils.args

@Suppress("LeakingThis")
abstract class Option<T> private constructor(
    val longForm: String,
    val withValue: Boolean,
    val isRequired: Boolean = false,
    val shortForm: String? = null,
    val helpDesc: String? = null
) {
    companion object {
        val longFormPattern = Regex("^([a-z](?:[a-z0-9_\\-]*[a-z0-9])?)$", RegexOption.IGNORE_CASE)
        val shortFormPattern = Regex("^[a-z]$", RegexOption.IGNORE_CASE)
    }

    init {
        if (!longFormPattern.matches(longForm)) {
            throw IllegalOptionNameException(this)
        }
        if (shortForm != null) {
            if (!shortFormPattern.matches(shortForm)) {
                throw IllegalOptionNameException(this)
            }
        }
    }

    protected constructor(
        longForm: String,
        withValue: Boolean,
        isRequired: Boolean,
        shortForm: Char? = null,
        helpDesc: String? = null
    ) :
            this(longForm, withValue, isRequired, shortForm?.toString(), helpDesc)

    fun getHelp(): String? =
            if (helpDesc == null) {
                null
            } else {
                val options = (if (shortForm != null) "-$shortForm, " else "") + "--" + longForm + ":"
                val tabs = 4 - (options.length / 4)
                options + "\t".repeat(tabs) + helpDesc +
                        (if (isRequired) " (is required)" else "")
            }

    @Throws(IllegalOptionValueException::class)
    fun setValue(arg: String) {
        if (this.withValue && arg == "") {
            throw IllegalOptionValueException(this, "")
        }
        value = parse(arg)
    }

    private var value: T? = null

    fun getValue(): T = value ?: throw UnsetOptionException(this)

    @Throws(IllegalOptionValueException::class)
    protected abstract fun parse(arg: String): T

    override fun toString(): String {
        return (if (shortForm != null) "-$shortForm, " else "") + "--" + longForm
    }
}

class StringOption(
    longForm: String,
    isRequired: Boolean,
    shortForm: Char? = null,
    helpDesc: String? = null
) : Option<String>(longForm, true, isRequired, shortForm, helpDesc) {

    override fun parse(arg: String): String = arg
}

class LongOption(
    longForm: String,
    isRequired: Boolean,
    shortForm: Char? = null,
    helpDesc: String? = null
) : Option<Long>(longForm, true, isRequired, shortForm, helpDesc) {

    @Throws(IllegalOptionValueException::class)
    override fun parse(arg: String): Long =
            arg.toLongOrNull() ?: throw IllegalOptionValueException(this, arg)
}

class IntegerOption(
    longForm: String,
    isRequired: Boolean,
    shortForm: Char? = null,
    helpDesc: String? = null
) : Option<Int>(longForm, true, isRequired, shortForm, helpDesc) {

    @Throws(IllegalOptionValueException::class)
    override fun parse(arg: String): Int =
            arg.toIntOrNull() ?: throw IllegalOptionValueException(this, arg)
}

class DoubleOption(
    longForm: String,
    isRequired: Boolean,
    shortForm: Char? = null,
    helpDesc: String? = null
) : Option<Double>(longForm, true, isRequired, shortForm, helpDesc) {

    @Throws(IllegalOptionValueException::class)
    override fun parse(arg: String): Double =
            arg.toDoubleOrNull() ?: throw IllegalOptionValueException(this, arg)
}

class BooleanOption(
    longForm: String,
    isRequired: Boolean,
    shortForm: Char? = null,
    helpDesc: String? = null
) : Option<Boolean>(longForm, false, isRequired, shortForm, helpDesc) {

    override fun parse(arg: String): Boolean = true
}
