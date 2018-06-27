package com.und.model.jpa

import com.und.model.MessageType
import com.und.model.Status
import java.time.LocalDateTime
import javax.persistence.*


@Entity
@Table(name = "sms_template")
class SmsTemplate {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "sms_template_id_seq")
    @SequenceGenerator(name = "sms_template_id_seq", sequenceName = "sms_template_id_seq", allocationSize = 1)
    var id: Long? = null

    @Column(name = "client_id")
    var clientID: Long? = null

    @Column(name = "appuser_id")
    var appuserID: Long? = null

    @Column(name = "name")
    lateinit var name: String

    @Column(name = "sms_template_body")
    lateinit var smsTemplateBody: String

    @Column(name = "parent_id")
    var parentID: Long? = null

    @Column(name = "from_user")
    lateinit var from: String

    @Column(name = "message_type") //Promotional or Transactional
    @Enumerated(EnumType.STRING)
    var messageType: MessageType? = null

    @Column(name = "tags")
    var tags: String? = null

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    lateinit var status: Status

    @Column(name = "date_created")
    lateinit var dateCreated: LocalDateTime

    @Column(name = "date_modified")
    lateinit var dateModified: LocalDateTime
}

