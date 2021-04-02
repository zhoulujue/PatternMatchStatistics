package tracker

import event.IEvent
import event.IValue

/**
 * 描述 Tracker 根据 Pattern 规则匹配到的结果
 * 结果为 IEvent 的集合
 */
class TrackerResult(
    private val events: List<IEvent>,
): IValue {

    companion object {
        fun create(events: List<IEvent>): TrackerResult? {
            return if (events.isEmpty()) null else TrackerResult(events)
        }
    }

    override fun statisticsValue(): String {
        return events.joinToString(separator = "/", transform = {
            it.statisticsType() + "_" + it.statisticsAction() + "_" + it.statisticsIdentifier()
        })
    }

    override fun toString(): String {
        return events.joinToString(separator = " \n")
    }
}