package com.und.model

import com.fasterxml.jackson.annotation.JsonProperty

class JobDescriptor {

    lateinit var clientId: String
    lateinit var campaignId: String

    var campaignName: String? = null

    @JsonProperty("triggers")
    var triggerDescriptors: List<TriggerDescriptor> = arrayListOf()


    var action:Action = Action.NOTHING

    enum class Action {
        PAUSE,CREATE,RESUME,DELETE,STOP,NOTHING,
    }
}