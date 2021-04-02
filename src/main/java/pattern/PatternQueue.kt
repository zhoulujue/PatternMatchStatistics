package pattern

import org.json.JSONArray
import patternmatch.PMConstant

class PatternQueue private constructor(
    private val eventPatterns: List<IPattern>
): Pattern() {

    companion object {
        fun create(eventPatterns: List<IPattern>): PatternQueue? {
            if (eventPatterns.isEmpty()) {
                return null
            }
            return PatternQueue(eventPatterns)
        }

        fun create(jsonArray: JSONArray): PatternQueue? {
            if (jsonArray.length() < 1) {
                return null
            }
            val patterns = ArrayList<IPattern>()
            for (i in 0 until jsonArray.length()) {
                jsonArray.optJSONObject(i)?.let {
                    patterns.add(Pattern.create(it))
                }
            }

            return create(patterns)
        }
     }

    override fun statisticsMatchQuantifier(): String {
        return eventPatterns[eventPatterns.size - 1].statisticsMatchQuantifier()
    }

    override fun toString(): String {
        return eventPatterns.joinToString(separator = " ==> ")
    }

    override fun getStringForPatternMatch(): String {
        return PMConstant.PM_CONSTANT_PATTERN_GROUP_LEFT_SYMBOL +
                eventPatterns.joinToString( separator = "", transform = {
                    it.getStringForPatternMatch()
                } ) +
                PMConstant.PM_CONSTANT_PATTERN_GROUP_RIGHT_SYMBOL
    }
}