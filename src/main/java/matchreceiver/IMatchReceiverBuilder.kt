package matchreceiver

import org.json.JSONObject

interface IMatchReceiverBuilder {
    fun build(name: String, jsonObject: JSONObject): IMatchReceiver
}