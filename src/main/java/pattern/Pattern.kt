package pattern

import event.Event
import event.IEvent
import org.json.JSONArray
import org.json.JSONObject
import patternmatch.PMRange
import patternmatch.PMConstant
import patternmatch.PMConstant.encodeReservedCharacter
import patternmatch.PatternMatcher


open class Pattern private constructor(
    private val type: String,
    private val actions: List<String>,
    private val identifiers: List<String>,
    private val actionsAreAccepted: Boolean,
    private val identifiersAreAccepted: Boolean,
    private val matchQuantifier: String
): IPattern {

    public constructor(): this("", emptyList(), emptyList(), true, true, "")

    private val matcher: PatternMatcher by lazy {
        PatternMatcher.create(this)
    }

    companion object {
        fun create(json: JSONObject): Pattern {
            val type = json.optString("type")

            // actions
            val actionsJA = json.optJSONArray("actions")
            val actions = ArrayList<String>()
            if (actionsJA != null) {
                for (i in 0 until actionsJA.length()) {
                    val action = actionsJA.optString(i)
                    action?.takeIf { action.isNotEmpty() }?.let {
                        actions.add(action)
                    }
                }
            }

            // actions_are_banned
            val actionsAreAccepted = !json.optBoolean("actions_are_banned", false)

            // ids
            val idsJA = json.optJSONArray("ids")
            val identifiers = ArrayList<String>()
            if (idsJA != null) {
                for (i in 0 until idsJA.length()) {
                    val id = idsJA.optString(i)
                    id?.takeIf { id.isNotEmpty() }?.let {
                        identifiers.add(id)
                    }
                }
            }

            // identifiers_are_banned
            val identifiersAreAccepted = !json.optBoolean("identifiers_are_banned", false)

            // quantifier
            val quantifier = json.optString("match_quantifier")

            return create(type, actions, identifiers, actionsAreAccepted, identifiersAreAccepted, quantifier)
        }

        fun create(type: String, actions: List<String>, identifiers: List<String>, actionsAreAccepted: Boolean, identifiersAreAccepted: Boolean, matchQuantifier: String): Pattern {
            val typeL = if (type.isEmpty()) Event.DYNAMIC_STATISTICS_BUILDIN_TOKENIZOR_ANY else type

            val matchQuantifierL = if (matchQuantifier.isEmpty()) "{1}" else matchQuantifier

            val actionsL = if (actions.isEmpty()) {
                ArrayList<String>().also{ it.add(Event.DYNAMIC_STATISTICS_BUILDIN_TOKENIZOR_ANY) }
            } else {
                actions
            }

            val identifiersL = if (identifiers.isEmpty()) {
                ArrayList<String>().also { it.add(Event.DYNAMIC_STATISTICS_BUILDIN_TOKENIZOR_ANY) }
            } else {
                identifiers
            }

            return Pattern(typeL, actionsL, identifiersL, actionsAreAccepted, identifiersAreAccepted, matchQuantifierL)
        }
    }

    override fun statisticsMatchQuantifier(): String {
        return matchQuantifier
    }

    override fun statisticsMatchEvents(events: List<IEvent>): PMRange {
        return matcher.rangeOfFirstMatchInPMObjects(events, PMRange(0, events.size))
    }

    override fun getStringForPatternMatch(): String {
        // 将 type 及 type的语义（例如none、any） 作为正则表达式的一个 group 先转化了
        val typeRegexPattern = PMConstant.PM_CONSTANT_PATTERN_GROUP_LEFT_SYMBOL +
                (if (type == Event.DYNAMIC_STATISTICS_BUILDIN_TOKENIZOR_ANY)
                    PMConstant.PM_CONSTANT_COMMON_PATTERN
                else
                    encodeReservedCharacter(type)) +
                PMConstant.PM_CONSTANT_PATTERN_GROUP_RIGHT_SYMBOL

        //将 action 及 action的语义（appear、disappear、crash）作为正则表达式的一个 group 先转化了
        val actionRegexPattern = (if (actionsAreAccepted) "" else PMConstant.PM_CONSTANT_COMPLEMENT) +
                PMConstant.PM_CONSTANT_PATTERN_GROUP_LEFT_SYMBOL +
                (if (actions.contains(Event.DYNAMIC_STATISTICS_BUILDIN_TOKENIZOR_ANY))
                    PMConstant.PM_CONSTANT_COMMON_PATTERN
                else
                    encodeReservedCharacter(actions.joinToString(separator = PMConstant.PM_CONSTANT_OR))) +
                PMConstant.PM_CONSTANT_PATTERN_GROUP_RIGHT_SYMBOL

        //转化identification 及 identification 的 语义
        val idRegexPattern = (if (identifiersAreAccepted) "" else PMConstant.PM_CONSTANT_COMPLEMENT) +
                PMConstant.PM_CONSTANT_PATTERN_GROUP_LEFT_SYMBOL +
                (if (identifiers.contains(Event.DYNAMIC_STATISTICS_BUILDIN_TOKENIZOR_ANY))
                    PMConstant.PM_CONSTANT_COMMON_PATTERN
                else
                    encodeReservedCharacter(identifiers.joinToString(separator = PMConstant.PM_CONSTANT_OR))) +
                PMConstant.PM_CONSTANT_PATTERN_GROUP_RIGHT_SYMBOL


        // 拼接用于匹配的字符串，例如：type_(action0|action1)_(id0|id1)_
        // NOTICE：下划线“_”是保留字符，需要PMConstant.encodeReservedCharacter方法来转义！
        return PMConstant.PM_CONSTANT_PATTERN_GROUP_LEFT_SYMBOL +
                PMConstant.PM_CONSTANT_PATTERN_GROUP_LEFT_SYMBOL +
                typeRegexPattern +
                PMConstant.PM_CONSTANT_PATTERN_OBJECT_KEY_ATTRIBUTE_SEPARATOR +
                actionRegexPattern +
                PMConstant.PM_CONSTANT_PATTERN_OBJECT_KEY_ATTRIBUTE_SEPARATOR +
                idRegexPattern +
                PMConstant.PM_CONSTANT_PATTERN_OBJECT_KEY_ATTRIBUTE_SEPARATOR +
                PMConstant.PM_CONSTANT_PATTERN_GROUP_RIGHT_SYMBOL +
                statisticsMatchQuantifier() +
                PMConstant.PM_CONSTANT_PATTERN_GROUP_RIGHT_SYMBOL
    }

    override fun toString(): String {
        return "{ type : $type , actions : $actions , accepted : $actionsAreAccepted , identifiers : $identifiers , accepted : $identifiersAreAccepted , matchQuantifier : $matchQuantifier }"
    }
}