package tracker

import event.Event
import event.IEvent
import matchreceiver.IMatchReceiver
import matchreceiver.MatchReceiverFactory
import org.json.JSONArray
import org.json.JSONObject
import pattern.IPattern
import pattern.PatternFactory
import patternmatch.PMConstant
import patternmatch.PMRange
import kotlin.math.max


class Tracker(
    public val identifier: String,
    private val acceptedContexts: List<String>,
    private val matchReceivers: List<IMatchReceiver>,
    public val pattern: IPattern
    ) {

    private var trackingEvents: MutableList<IEvent> = ArrayList()

    companion object {
        private const val EVENTS_TRACKING_RANGE_LIMIT = 64

        fun create(id: String, json: JSONObject): Tracker? {
            if (id.isEmpty()) {
                return null
            }
            // Pattern
            var pattern: IPattern?
            val patternJsonArray: JSONArray? = json.optJSONArray("pattern")
            // 如果结构是 { "pattern": [ pattern1, pattern2, pattern3... ] }
            pattern = if (patternJsonArray == null) null else PatternFactory.create(patternJsonArray)
            if (pattern == null) {
                json.optJSONObject("pattern")?.let{
                    pattern = PatternFactory.create(it)
                }
            }

            // MatchReceiver
            val receiversJsonArray: JSONArray? = json.optJSONArray("match_receivers")
            var receivers = ArrayList<JSONObject>()
            receiversJsonArray?.takeIf { it.length() > 0 }?.let {
                for (i in 0 until it.length()) {
                    it.optJSONObject(i)?.let { jo -> receivers.add(jo) }
                }
            }

            // Accepted_contexts
            val contexts = ArrayList<String>()
            val contextsJsonArray = json.optJSONArray("accepted_contexts")
            contextsJsonArray?.takeIf { it.length() > 0 }?.let {
                for (i in 0 until it.length()) {
                    it.optString(i)?.takeIf { str -> str.isNotEmpty() }?.let { context -> contexts.add(context) }
                }
            }

            return pattern?.let { create(id, it, receivers, contexts) }
        }

        fun create(id: String, pattern: IPattern, matchReceivers: List<JSONObject>, acceptedContexts: List<String>): Tracker? {
            if (id.isEmpty() || matchReceivers.isEmpty()) {
                return null
            }

            val receivers = ArrayList<IMatchReceiver>()
            for (json: JSONObject in matchReceivers) {
                MatchReceiverFactory.create(json)?.let { receivers.add(it) }
            }

            val contexts = if (acceptedContexts.isEmpty()) {
                val cts = ArrayList<String>()
                cts.add(Event.DYNAMIC_STATISTICS_BUILDIN_TOKENIZOR_ANY)
                cts
            } else acceptedContexts

            return Tracker(id, contexts, receivers, pattern)
        }
    }

    fun didReceive(event: IEvent?, context: String) {
        if (!acceptedContexts.contains(context) && !acceptedContexts.contains(Event.DYNAMIC_STATISTICS_BUILDIN_TOKENIZOR_ANY)) {
            return
        }
        if (event == null) {
            return
        }

        addEvent(trackingEvents, event)
        val trackedRange = pattern.statisticsMatchEvents(trackingEvents)

        println("-------trackingEvents-------: ${trackingEvents.joinToString(separator = ", ")}")
        println("-------trackedRange-------: $trackedRange")

        if (trackedRange.location != PMConstant.PM_NOT_FOUND) {
            val events = ArrayList<IEvent>(trackingEvents.subList(trackedRange.location, trackedRange.location + trackedRange.length))

            val trimStartIndex = trackedRange.location + trackedRange.length
            val trimMaxIndex = trackingEvents.size - 1
            if (trimStartIndex < trimMaxIndex) {
                trackingEvents = trackingEvents.subList(trimStartIndex, trimMaxIndex)
            } else {
                trackingEvents.clear()
            }

            TrackerResult.create(events)?.let {
                notify(it, context)
            }
        }


    }

    private fun notify(result: TrackerResult, context: String) {
        for (receiver: IMatchReceiver in matchReceivers) {
            receiver.onMatchResultReceived(result, context, identifier)
        }
    }

    private fun addEvent(events: MutableList<IEvent>?, e: IEvent?) {
        if (e == null || events == null) {
            return
        }
        if (events.size == EVENTS_TRACKING_RANGE_LIMIT) {
            events.removeAt(0)
        }
        events.add(e)
    }

}