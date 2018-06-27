package com.und.model.web

import com.und.model.mongo.LineItem
import java.util.*

open class EventWeb {

    lateinit var name: String
    var clientId: Int = -1
    var identity: Identity = Identity()
    var ipAddress: String? = null
    var city: String? = null
    var state: String? = null
    var country: String? = null
    var latitude: String? = null
    var longitude: String? = null
    var agentString: String? = null
    var userIdentified: Boolean = false
    var lineItem: MutableList<LineItem> = mutableListOf()
    var attributes: HashMap<String, Any> = hashMapOf()

    //var creationTime: Long = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli()

}

data class Identity(
        //unique id assigned to a device, should always remain fixed, create new if not found
        var deviceId: String = "",
        //if userId is not found assign a new session id, handle change if user login changes, logouts etc
        var sessionId: String = "",
        // id of event user, this id is assigned when a user profile is identified.
        var userId: String? = null,
        var clientId: Int? = -1
)

class date : EventWeb() {
    //TODO
}

/* {
    var eventUser: EventUser = EventUser()
}
*/





