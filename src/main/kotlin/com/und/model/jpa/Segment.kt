package com.und.model.jpa

import com.und.model.web.DidEvents
import com.und.model.web.Geography
import com.und.model.web.GlobalFilter
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "segment")
class Segment {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "segment_id_seq")
    @SequenceGenerator(name = "segment_id_seq", sequenceName = "segment_id_seq", allocationSize = 1)
    var id: Long? = null

    @Column(name = "client_id")
    var clientID: Long? = null

    @Column(name = "appuser_id")
    var appuserID: Long? = null

    @Column(name = "name")
    var name: String = ""

    @Column(name = "type")
    var type: String = ""

    @Column(name = "date_created")
    lateinit var dateCreated: LocalDateTime

    @Column(name = "date_modified")
    lateinit var dateModified: LocalDateTime

    @Column(name = "conversion_event")
    var conversionEvent: String = ""

    @Column(name = "data")
    var data: String = "{}"


}

class SegmentData {
    var conversionEvent: String = ""
    var didEvents: DidEvents = DidEvents()
    var didNotEvents: DidEvents = DidEvents()
    var globalFilters: List<GlobalFilter> = listOf()
    var geographyFilters: List<Geography> = listOf()
}