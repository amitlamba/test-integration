package com.und.repository

import com.und.model.jpa.CampaignAuditLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CampaignAuditLogRepository : JpaRepository<CampaignAuditLog, Long>