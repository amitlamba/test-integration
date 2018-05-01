package com.und.web

import com.und.mongo.LineItem
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

/**
 * Created by shiv on 21/07/17.
 */

//FIXME handle validation for other fields and different types, e.g. @see
//@ValidateDate
open class EventWeb {


    lateinit var name: String


    var clientId: Int = -1

    var identity: Identity = Identity()
    //var creationTime: Long = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli()


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


    var startDate= LocalDate.now()


    var endDate=LocalDate.of(2017, 1, 13)

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





