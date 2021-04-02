package matchreceiver

import org.json.JSONObject

object MatchReceiverFactory {
    private val builderMap: MutableMap<String, IMatchReceiverBuilder> = HashMap()

    fun create(jsonObject: JSONObject): IMatchReceiver? {
        val name = jsonObject.optString("id")
        if (name?.isEmpty() == true) {
            return null
        }
        val builder = builderMap[name]
        builder ?: return null
        return builder.build(name, jsonObject)
    }

    fun register(builder: IMatchReceiverBuilder, name: String) {
        if (name.isEmpty()) {
            return
        }
        println("MatchReceiverBuild registering: builder: ${builder}, name: ${name}.")
        builderMap[name] = builder
    }
}