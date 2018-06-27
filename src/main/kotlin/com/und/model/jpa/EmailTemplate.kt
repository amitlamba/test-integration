package com.und.model.jpa

import com.und.model.MessageType

import java.time.LocalDateTime
import javax.persistence.*


@Entity
@Table(name = "email_template")
class EmailTemplate {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "email_template_id_seq")
    @SequenceGenerator(name = "email_template_id_seq", sequenceName = "email_template_id_seq", allocationSize = 1)
    var id: Long? = null

    @Column(name = "client_id")
    var clientID: Long? = null

    @Column(name = "appuser_id")
    var appuserID: Long? = null

    @Column(name = "name")
    lateinit var name: String

    @Column(name = "email_template_body")
    lateinit var emailTemplateBody: String

    @Column(name = "email_template_subject")
    lateinit var emailTemplateSubject: String

    @Column(name = "parent_id")
    var parentID: Long? = null

    //@Email
    @Column(name = "from_user")
    lateinit var from: String

    @Column(name = "message_type") //Promotional or Transactional
    @Enumerated(EnumType.STRING)
    var messageType: MessageType? = null

    @Column(name = "tags")
    var tags: String? = null

    @Column(name = "date_created")
    lateinit var dateCreated: LocalDateTime

    @Column(name = "date_modified")
    lateinit var dateModified: LocalDateTime

//    @Column(name = "status")
//    @NotNull
//    @Enumerated(EnumType.STRING)
//    lateinit var status: Status
}

