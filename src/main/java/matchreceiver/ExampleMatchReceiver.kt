package matchreceiver

import org.json.JSONObject
import tracker.TrackerResult

class ExampleMatchReceiver: IMatchReceiver {
    override fun onMatchResultReceived(result: TrackerResult, context: String, trackerID: String) {
        println("MatchReceiver(ExampleMatchReceiver) is processing TrackerResult: ${result}, with context: ${context}, trackerID: ${trackerID}.")
    }

    class Builder: IMatchReceiverBuilder {
        override fun build(name: String, jsonObject: JSONObject): IMatchReceiver {
            return ExampleMatchReceiver()
        }
    }
}