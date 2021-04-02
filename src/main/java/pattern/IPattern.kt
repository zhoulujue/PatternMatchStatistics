package pattern

import event.IEvent
import patternmatch.IPMPattern
import patternmatch.PMRange

/**
 * 描述一个匹配规则
 * 向外提供两个方法：
 * - statisticsMatchEvents: 将输入的事件流进行匹配，输出匹配结果-PMRange
 * - statisticsMatchQuantifier: 描述本规则可连续匹配的数量
 */
interface IPattern: IPMPattern {
    fun statisticsMatchQuantifier(): String
    fun statisticsMatchEvents(events: List<IEvent>): PMRange
}