package com.und.model.jpa

import com.und.model.Status
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "service_provider_credentials")
class ServiceProviderCredentials {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "service_provider_credentials_id_seq")
    @SequenceGenerator(name = "service_provider_credentials_id_seq", sequenceName = "service_provider_credentials_id_seq", allocationSize = 1)
    var id: Long? = null

    @Column(name = "client_id")
    var clientID: Long? = null

    @Column(name = "appuser_id")
    var appuserID: Long? = null

    @Column(name = "service_provider_type")
    lateinit var serviceProviderType: String

    @Column(name = "service_provider")
    lateinit var serviceProvider: String

    @Column(name = "date_created")
    lateinit var dateCreated: LocalDateTime

    @Column(name = "date_modified")
    lateinit var dateModified: LocalDateTime

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    lateinit var status: Status

    @Column(name = "credentials")
    lateinit var credentialsMap: String
}

