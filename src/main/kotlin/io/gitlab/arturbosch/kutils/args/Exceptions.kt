package io.gitlab.arturbosch.kutils.args

abstract class OptionException(description: String) : Exception(description)

class NotFlagException(
    options: String,
    option: Option<*>
) : UnknownOptionException(
    options,
    "Illegal option: -" + option.shortForm + " requires a value in '" + options + "'"
)

class IllegalOptionValueException(
    option: Option<*>,
    val value: String
) : OptionException("Illegal value '$value' for option $option")

class IllegalOptionNameException(option: Option<*>) : OptionException("Illegal name option $option")

class RequiredOptionException(option: Option<*>) : OptionException("Option $option is required ")

class UnsetOptionException(option: Option<*>) : OptionException("Option $option is not yet parsed.")

open class UnknownOptionException(
    optionLong: String,
    description: String = "Unknown option '$optionLong'"
) : OptionException(description)

class UnknownSubOptionException(
    options: String,
    subOption: Char
) : UnknownOptionException(
    options,
    "Unknown option: '$subOption' in '$options'"
)
