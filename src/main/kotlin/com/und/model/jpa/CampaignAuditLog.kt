package com.und.model.jpa


import com.und.model.JobActionStatus
import com.und.model.JobDescriptor
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "campaign_audit_log")
class CampaignAuditLog {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "campaign_audit_log_id_seq")
    @SequenceGenerator(name = "campaign_audit_log_id_seq", sequenceName = "campaign_audit_log_id_seq", allocationSize = 1)
    var id: Long? = null

    @Column(name = "client_id")
    var clientID: Long? = null

    @Column(name = "campaign_id")
    var campaignId: Long = 0

    @Column(name = "message")
    var message: String = ""


    @Column(name = "status", updatable = false)
    @Enumerated(EnumType.STRING)
    lateinit var status:JobActionStatus.Status

    @Column(name = "action", updatable = false)
    @Enumerated(EnumType.STRING)
    lateinit var action: JobDescriptor.Action


    @Column(name = "date_created")
    lateinit var dateCreated: LocalDateTime


}
