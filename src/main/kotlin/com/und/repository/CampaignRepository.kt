package com.und.repository

import com.und.model.jpa.Campaign
import com.und.model.jpa.CampaignType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CampaignRepository : JpaRepository<Campaign, Long> {
    fun findByClientID(clientID: Long): List<Campaign>
    fun findByIdAndClientID(id: Long, clientID: Long): Campaign
    fun findByClientIDAndCampaignType(clientID: Long, campaignType: CampaignType): List<Campaign>
    fun findByIdAndClientIDAndCampaignType(id: Long, clientID: Long, campaignType: CampaignType): Campaign

}