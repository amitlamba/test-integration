package com.und.model.web

import com.und.model.Status
import java.util.HashMap

class ServiceProviderCredentials {
    var id: Long? = null
    var clientID: Long? = null
    var appuserID: Long? = null
    lateinit var serviceProviderType: String
    lateinit var serviceProvider: String
    lateinit var status: Status
    var credentialsMap: HashMap<String, String> = HashMap()
}