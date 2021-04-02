package matchreceiver

import tracker.TrackerResult

interface IMatchReceiver {
    fun onMatchResultReceived(result: TrackerResult, context: String, trackerID: String)
}