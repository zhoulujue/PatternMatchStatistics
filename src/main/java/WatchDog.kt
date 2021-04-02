import event.Event
import matchreceiver.ExampleMatchReceiver
import matchreceiver.MatchReceiverFactory
import org.json.JSONObject
import tracker.Tracker

object WatchDog {

    private val trackers: MutableList<Tracker> = ArrayList()

    init {
        // Register necessary match receivers
        MatchReceiverFactory.register(ExampleMatchReceiver.Builder(), "example")
    }

    fun applyConfig(config: String) {
        var rootConfigJson: JSONObject? = null
        try {
            rootConfigJson = JSONObject(config)
        } catch (ignored: Throwable) {

        }
        if (rootConfigJson == null) {
            return
        }

        val trackerIDJsonArray = rootConfigJson.optJSONArray("enabled_trackers")
        if (trackerIDJsonArray == null || trackerIDJsonArray.length() == 0) {
            return
        }

        for (i in 0 until trackerIDJsonArray.length()) {
            val trackerID = trackerIDJsonArray.optString(i)
            val trackerJson = rootConfigJson.optJSONObject(trackerID) ?: continue

            val tracker = Tracker.create(trackerID, trackerJson) ?: return
            registerTracker(tracker)
        }
    }

    private fun registerTracker(tracker: Tracker) {
        val id = tracker.identifier
        var len = trackers.size
        var i = 0
        while (i < len) {
            val t = trackers[i]
            if (t.identifier == id) {
                trackers.removeAt(i)
                --len
                --i
            }
            ++i
        }
        trackers.add(tracker)
    }

    public fun enqueueEvent(type: String, action: String, identifier: String, context: String) {
        enqueueEvent(type, action, identifier, context, null)
    }

    public fun enqueueEvent(type: String, action: String, identifier: String, context: String, customInfo: Map<String, Any>?) {
        val event = Event(type, action, identifier, customInfo)



        for (tracker in trackers) {
            tracker.didReceive(event, context)
        }
    }
}