import java.time.LocalDateTime
import java.time.ZoneId
import java.util.HashMap


class Event {

    var id: String? = null
    var name: String? =null
    var clientId: Int?=null
    var lineItem: MutableList<LineItem> = mutableListOf()
    var attributes: HashMap<String, Any> = hashMapOf()
    var system: System = System()
    var creationTime: LocalDateTime = LocalDateTime.now()
    var geoDetails = GeoDetails()
    var deviceId: String = ""
    var userIdentified: Boolean = false
    var userId: String? = null
    var sessionId: String = ""

    var geogrophy: Geogrophy? = null
}

class Coordinate{
    val latitude: Float= 0.0F
    val longitude: Float = 0.0f
}
class GeoLocation{
    val type: String = "Point"
    val coordinate: Coordinate? = null
}
class GeoDetails {
    var ip: String? = null
    var geolocation: GeoLocation? = null
}

class SystemDetails {
    val name: String?=null
    val version: String?=null
}
class System {
    var os: SystemDetails? = null
    var browser: SystemDetails? = null
    var device: SystemDetails? = null
    var application: SystemDetails? = null
}


class LineItem {
    var price: Int = 0
    var currency: String? = null
    var product: String? = null
    var categories: MutableList<String> = mutableListOf()
    var tags: MutableList<String> = mutableListOf()
    var quantity: Int = 0
    var properties: HashMap<String, Any> = hashMapOf()
}

class Geogrophy {
    val country: String?=null
    val state: String?=null
    val city: String?=null
}