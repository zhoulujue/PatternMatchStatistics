package event

import patternmatch.IPMObject

/**
 * 描述观察队列里的一个抽象事件.
 * 一般来说，我们描述一个事件是的语境是 "在某个页面发生了一件事情"
 * Actor 表示事件的主体，例如 article
 * Action 表示实际的行为
 * CustomInfo 表示附属信息，是一个键值对集合
 * PageIdentification 用来描述事件发生的位置，一般是一个页面的相对地址 例如 w/xxx、creation_center
 */
interface IEvent: IValue, IPageIdentification, IPMObject {
    fun statisticsType(): String
    fun statisticsAction(): String
    fun statisticsCustomInfo(): Map<String, Any>
}