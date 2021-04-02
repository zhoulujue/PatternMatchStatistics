package event

import patternmatch.PMConstant

class Event(
    val type: String,
    val action: String,
    val identifier: String,
    val customInfo: Map<String, Any>?
): IEvent {

    companion object {
        val DYNAMIC_STATISTICS_BUILDIN_TOKENIZOR_NONE = "none"
        val DYNAMIC_STATISTICS_BUILDIN_TOKENIZOR_ANY = "any"
        val DYNAMIC_STATISTICS_BUILDIN_EMPTY_PATH = "empty_path"
        val DYNAMIC_STATISTICS_BUILDIN_EVENT_TOKENIZOR_ACTION_APPEAR = "appear"
        val DYNAMIC_STATISTICS_BUILDIN_EVENT_TOKENIZOR_ACTION_DISAPPEAR = "disappear"
        val DYNAMIC_STATISTICS_BUILDIN_EVENT_TOKENIZOR_ACTION_CRASH = "crash"
    }

    override fun statisticsType(): String {
        return type
    }

    override fun statisticsAction(): String {
        return action
    }

    override fun statisticsCustomInfo(): Map<String, Any> {
        return customInfo ?: emptyMap()
    }

    override fun statisticsValue(): String {
        return "${type}_${action}_${identifier}"
    }

    override fun statisticsIdentifier(): String {
        return identifier
    }

    override fun statisticsRelativePath(): String {
        return DYNAMIC_STATISTICS_BUILDIN_EMPTY_PATH
    }

    override fun hashStringForPatternMatch(): String {
        return "${PMConstant.encodeReservedCharacter(type)}_${PMConstant.encodeReservedCharacter(action)}_${PMConstant.encodeReservedCharacter(identifier)}_"
    }

    override fun toString(): String {
        return "{ type : $type , action : $action identifier : $identifier }"
    }

}