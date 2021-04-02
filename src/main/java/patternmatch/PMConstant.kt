package patternmatch

object PMConstant {
    const val PM_CONSTANT_PATTERN_GROUP_LEFT_SYMBOL = "("
    const val PM_CONSTANT_PATTERN_GROUP_RIGHT_SYMBOL = ")"
    const val PM_CONSTANT_COMMON_PATTERN = "[^_]+"
    const val PM_CONSTANT_COMPLEMENT = "^"
    const val PM_CONSTANT_OR = "|"
    const val PM_CONSTANT_PATTERN_OBJECT_KEY_ATTRIBUTE_SEPARATOR = "_"

    const val PM_NOT_FOUND = -1

    fun encodeReservedCharacter(stringWithReservedCharacters: String): String {
        return stringWithReservedCharacters.replace(PM_CONSTANT_PATTERN_OBJECT_KEY_ATTRIBUTE_SEPARATOR, "%0")
    }
}