package pattern

import org.json.JSONObject
import patternmatch.PMConstant

class PatternGroup private constructor(
    private val matchQuantifier: String,
    private val eventPatterns: List<IPattern>,
): Pattern() {
    companion object {
        fun create(json: JSONObject): PatternGroup? {
            val patternsJson = json.optJSONArray("patterns") ?: return null
            val patterns = ArrayList<IPattern>()
            for (i in 0 until patternsJson.length()) {
                patternsJson.optJSONObject(i)?.let { patternJo -> patterns.add(Pattern.create(patternJo)) }
            }
            val quantifier = json.optString("match_quantifier")
            return create(quantifier, patterns)
        }

        fun create(matchQuantifier: String, eventPatterns: List<IPattern>): PatternGroup? {
            if (eventPatterns.isEmpty()) {
                return null
            }
            val quantifier = if (matchQuantifier.isEmpty()) "{1}" else matchQuantifier
            return PatternGroup(quantifier, eventPatterns)
        }

    }

    override fun statisticsMatchQuantifier(): String {
        return matchQuantifier
    }

    override fun toString(): String {
        val patternsStr = eventPatterns.joinToString(separator = " || ")
        return "${patternsStr}, match_quantifier : ${matchQuantifier}"
    }

    override fun getStringForPatternMatch(): String {
        var resultStr = ""
        resultStr += PMConstant.PM_CONSTANT_PATTERN_GROUP_LEFT_SYMBOL
        resultStr += PMConstant.PM_CONSTANT_PATTERN_GROUP_LEFT_SYMBOL

        resultStr += eventPatterns.joinToString(separator = PMConstant.PM_CONSTANT_OR)

        resultStr += PMConstant.PM_CONSTANT_PATTERN_GROUP_RIGHT_SYMBOL
        resultStr += PMConstant.encodeReservedCharacter(matchQuantifier)
        resultStr += PMConstant.PM_CONSTANT_PATTERN_GROUP_RIGHT_SYMBOL
        return resultStr
    }
}