package com.und.model.web

import com.und.model.MessageType
import java.time.LocalDateTime

class EmailTemplate {

    var id: Long? = null
    var clientID: Long? = null
    var appuserID: Long? = null
    lateinit var name: String
    lateinit var emailTemplateBody: String
    lateinit var emailTemplateSubject: String
    var parentID: Long? = null
    lateinit var from: String
    var messageType: MessageType? = null
    var tags: String? = null
    //lateinit var dateCreated: LocalDateTime
    //lateinit var dateModified: LocalDateTime
    //lateinit var status: Status

}