package com.und.mongo

import java.time.LocalDateTime
import java.util.*

class EventUser {
    var id: String? = null
    var clientId: String? = null //client id , user is associated with, this can come from collection
    var clientUserId: String? = null//this is id of the user client has provided
    var socialId: SocialId = SocialId()
    var standardInfo: StandardInfo = StandardInfo()
    var additionalInfo: HashMap<String, Any> = hashMapOf()
    var creationDate: LocalDateTime = LocalDateTime.now()


}

class SocialId {

    var fbId: String? = null
    var googleId: String? = null
    var mobile: String? = null
    var email: String? = null


}

class StandardInfo {
    var firstName: String? = null
    var lastName: String? = null
    var gender: String? = null
    var dob: String? = null
    var country: String? = null
    var countryCode: String? = null


}



