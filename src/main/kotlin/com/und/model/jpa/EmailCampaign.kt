package com.und.model.jpa


import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "email_campaign")
class EmailCampaign {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "email_campaign_id_seq")
    @SequenceGenerator(name = "email_campaign_id_seq", sequenceName = "email_campaign_id_seq", allocationSize = 1)
    var emailCampaignId: Long? = null

    @Column(name = "client_id")
    var clientID: Long? = null

    @Column(name = "appuser_id")
    var appuserId: Long? = null

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id")
    lateinit var campaign: Campaign

    @Column(name = "email_template_id")
    var templateId: Long? = null

    @Column(name = "date_created")
    lateinit var dateCreated: LocalDateTime

    @Column(name = "date_modified")
    lateinit var dateModified: LocalDateTime

}