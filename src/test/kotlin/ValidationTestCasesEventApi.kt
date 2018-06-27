import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.mongodb.BasicDBObject
import com.mongodb.MongoClient
import com.mongodb.client.MongoDatabase
import com.und.model.mongo.Event
import com.und.model.mongo.EventUser
import com.und.model.web.EventUserWeb
import com.und.model.web.EventWeb
import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.specification.RequestSpecification
import org.apache.http.HttpStatus
import org.bson.json.JsonMode
import org.bson.json.JsonWriterSettings
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.File
import java.io.StringReader
import java.util.*

class ValidationTestCasesEventApi {

    private var token = ""                      //Variable to hold Authorization token
    private var eventToken = ""                 //Variable to hold Event Authorization token
    private var deviceId = ""                   //Variable to hold Device Id of the first time user
    private var sessionId = ""                  //Variable to hold Session Id of the first time user

    private val userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36"
    private lateinit var objectMapper: ObjectMapper
    private val profile = "dev"                 //change this to dev to use local urls

    private lateinit var authUrl: String
    private lateinit var authEventUrl: String
    private lateinit var eventUrl: String
    private lateinit var pushProfileUrl: String
    private lateinit var eventInitializeUrl: String


    @Before
    fun runBeforeClass() {

        val properties = Properties()
        properties.load(StringReader(readFileText("profiles/$profile.properties")))
        authUrl = properties.getProperty("authUrl")
        authEventUrl=properties.getProperty("authEventUrl")
        eventUrl = properties.getProperty("eventUrl")
        pushProfileUrl = properties.getProperty("pushProfileUrl")
        eventInitializeUrl = properties.getProperty("eventInitializeUrl")
        objectMapper = objectMapper()

        //Generating Token
        val loginDetails = """{"username":"kamalpreetsingh025@gmail.com","password":"Kamalpreet123!"}"""
        val request = requestSpecificationWithOutAuth(loginDetails)
        val response = request.post(authUrl)
        token = response.jsonPath().get<String>("data.value.token")

        //Generating Event User Token
        val eventTokenJson="{}"
        val requestEventToken=requestSpecificationWithAuth(eventTokenJson,token)
        val responseEventToken=requestEventToken.post(authEventUrl)
        eventToken=responseEventToken.jsonPath().get<String>("data.value.token")

        //Retrieving DeviceId, SessionId, UserId for the first time user
        val eventJson = "{}"
        val requestEvent = requestSpecificationWithAuth(eventJson)
        val responseEvent = requestEvent.post(eventInitializeUrl)

        deviceId = responseEvent.jsonPath().get<String>("data.value.deviceId")
        sessionId = responseEvent.jsonPath().get<String>("data.value.sessionId")

    }


    @Test
    fun testInvalidEventNameSize(){
        //Invalid event name size
        val invalidEventNameJson = buildEvent("validation/invalidEventName.json",deviceId,sessionId)
        val requestEvent = requestSpecificationWithAuth(invalidEventNameJson,eventToken)
        val responseEvent = requestEvent.post(eventUrl)
        assertEquals(HttpStatus.SC_BAD_REQUEST, responseEvent.statusCode)
    }

    @Test
    fun testInvalidCityName(){
        //Invalid city name
        val invalidCityNameJson = buildEvent("validation/invalidCityName.json",deviceId,sessionId)
        val requestEvent = requestSpecificationWithAuth(invalidCityNameJson,eventToken)
        val responseEvent = requestEvent.post(eventUrl)
        assertEquals(HttpStatus.SC_BAD_REQUEST, responseEvent.statusCode)
    }

    @Test
    fun testInvalidCityNameSize(){
        //Invalid city name size
        val invalidCitySizeJson = buildEvent("validation/invalidCitySize.json",deviceId,sessionId)
        val requestEvent = requestSpecificationWithAuth(invalidCitySizeJson,eventToken)
        val responseEvent = requestEvent.post(eventUrl)
        assertEquals(HttpStatus.SC_BAD_REQUEST, responseEvent.statusCode)
    }

    @Test
    fun testInvalidStateName(){
        //Invalid state name
        val invalidStateNameJson = buildEvent("validation/invalidStateName.json",deviceId,sessionId)
        val requestEvent = requestSpecificationWithAuth(invalidStateNameJson,eventToken)
        val responseEvent = requestEvent.post(eventUrl)
        assertEquals(HttpStatus.SC_BAD_REQUEST, responseEvent.statusCode)
    }

    @Test
    fun testInvalidStateNameSize(){
        //Invalid state name size
        val invalidStateSizeJson = buildEvent("validation/invalidStateSize.json",deviceId,sessionId)
        val requestEvent = requestSpecificationWithAuth(invalidStateSizeJson,eventToken)
        val responseEvent = requestEvent.post(eventUrl)
        assertEquals(HttpStatus.SC_BAD_REQUEST, responseEvent.statusCode)
    }

    @Test
    fun testInvalidCountryName(){
        //Invalid country name
        val invalidCountryNameJson = buildEvent("validation/invalidCountryName.json",deviceId,sessionId)
        val requestEvent = requestSpecificationWithAuth(invalidCountryNameJson,eventToken)
        val responseEvent = requestEvent.post(eventUrl)
        assertEquals(HttpStatus.SC_BAD_REQUEST, responseEvent.statusCode)
    }

    @Test
    fun testInvalidCountryNameSize(){
        //Invalid country name size
        val invalidCountrySizeJson = buildEvent("validation/invalidCountrySize.json",deviceId,sessionId)
        val requestEvent = requestSpecificationWithAuth(invalidCountrySizeJson,eventToken)
        val responseEvent = requestEvent.post(eventUrl)
        assertEquals(HttpStatus.SC_BAD_REQUEST, responseEvent.statusCode)
    }

    @Test
    fun testInvalidLatitude(){
        //Invalid latitude
        val invalidLatitudeJson = buildEvent("validation/invalidLatitude.json",deviceId,sessionId)
        val requestEvent = requestSpecificationWithAuth(invalidLatitudeJson,eventToken)
        val responseEvent = requestEvent.post(eventUrl)
        assertEquals(HttpStatus.SC_BAD_REQUEST, responseEvent.statusCode)
    }

    @Test
    fun testInvalidLongitude(){
        //Invalid longitude
        val invalidLongitudeJson = buildEvent("validation/invalidLongitude.json",deviceId,sessionId)
        val requestEvent = requestSpecificationWithAuth(invalidLongitudeJson,eventToken)
        val responseEvent = requestEvent.post(eventUrl)
        assertEquals(HttpStatus.SC_BAD_REQUEST, responseEvent.statusCode)
    }

    @Test
    fun testInvalidFirstName(){
        //Invalid first name
        val invalidFirstNameJson = buildUserProfile("validation/invalidFirstName.json",deviceId,sessionId)
        val requestEvent = requestSpecificationWithAuth(invalidFirstNameJson,eventToken)
        val responseEvent = requestEvent.post(pushProfileUrl)
        assertEquals(HttpStatus.SC_BAD_REQUEST, responseEvent.statusCode)
    }

    @Test
    fun testInvalidFirstNameSize(){
        //Invalid first name size
        val invalidFirstNameSizeJson = buildUserProfile("validation/invalidFirstNameSize.json",deviceId,sessionId)
        val requestEvent = requestSpecificationWithAuth(invalidFirstNameSizeJson,eventToken)
        val responseEvent = requestEvent.post(pushProfileUrl)
        assertEquals(HttpStatus.SC_BAD_REQUEST, responseEvent.statusCode)
    }

    @Test
    fun testInvalidLastName(){
        //Invalid last name
        val invalidLastNameJson = buildUserProfile("validation/invalidLastName.json",deviceId,sessionId)
        val requestEvent = requestSpecificationWithAuth(invalidLastNameJson,eventToken)
        val responseEvent = requestEvent.post(pushProfileUrl)
        assertEquals(HttpStatus.SC_BAD_REQUEST, responseEvent.statusCode)
    }

    @Test
    fun testInvalidLastNameSize(){
        //Invalid last name size
        val invalidLastNameSizeJson = buildUserProfile("validation/invalidLastNameSize.json",deviceId,sessionId)
        val requestEvent = requestSpecificationWithAuth(invalidLastNameSizeJson,eventToken)
        val responseEvent = requestEvent.post(pushProfileUrl)
        assertEquals(HttpStatus.SC_BAD_REQUEST, responseEvent.statusCode)
    }

    @Test
    fun testInvalidCountryNameUserProfile(){
        //Invalid country name
        val invalidCountryNameJson = buildUserProfile("validation/invalidCountryName.json",deviceId,sessionId)
        val requestEvent = requestSpecificationWithAuth(invalidCountryNameJson,eventToken)
        val responseEvent = requestEvent.post(pushProfileUrl)
        assertEquals(HttpStatus.SC_BAD_REQUEST, responseEvent.statusCode)
    }

    @Test
    fun testInvalidCountryNameSizeUserProfile(){
        //Invalid country name size
        val invalidCountrySizeJson = buildUserProfile("validation/invalidCountrySize.json",deviceId,sessionId)
        val requestEvent = requestSpecificationWithAuth(invalidCountrySizeJson,eventToken)
        val responseEvent = requestEvent.post(pushProfileUrl)
        assertEquals(HttpStatus.SC_BAD_REQUEST, responseEvent.statusCode)
    }

    @Test
    fun testInvalidMobileNo(){
        //Invalid mobile number
        val invalidMobileJson = buildUserProfile("validation/invalidMobile.json",deviceId,sessionId)
        val requestEvent = requestSpecificationWithAuth(invalidMobileJson,eventToken)
        val responseEvent = requestEvent.post(pushProfileUrl)
        assertEquals(HttpStatus.SC_BAD_REQUEST, responseEvent.statusCode)
    }

    @Test
    fun testInvalidMobileNoSize(){
        //Invalid mobile number size
        val invalidMobileSizeJson = buildUserProfile("validation/invalidMobileSize.json",deviceId,sessionId)
        val requestEvent = requestSpecificationWithAuth(invalidMobileSizeJson,eventToken)
        val responseEvent = requestEvent.post(pushProfileUrl)
        assertEquals(HttpStatus.SC_BAD_REQUEST, responseEvent.statusCode)
    }

    @Test
    fun testInvalidDob(){
        //Invalid date of birth
        val invalidDobJson = buildUserProfile("validation/invalidDob.json",deviceId,sessionId)
        val requestEvent = requestSpecificationWithAuth(invalidDobJson,eventToken)
        val responseEvent = requestEvent.post(pushProfileUrl)
        assertEquals(HttpStatus.SC_BAD_REQUEST, responseEvent.statusCode)
    }

    /*@Test
    fun testInvalidGender(){
        //Invalid gender
        val invalidGenderJson = buildUserProfile("invalidGender.json",deviceId,sessionId)
        val requestEvent = requestSpecificationWithAuth(invalidGenderJson,eventToken)
        val responseEvent = requestEvent.post(pushProfileUrl)
        assertEquals(HttpStatus.SC_BAD_REQUEST, responseEvent.statusCode)
    }*/

    @Test
    fun testInvalidEmailId(){
        //Invalid email id
        val invalidEmailJson = buildUserProfile("validation/invalidEmail.json",deviceId,sessionId)
        val requestEvent = requestSpecificationWithAuth(invalidEmailJson,eventToken)
        val responseEvent = requestEvent.post(pushProfileUrl)
        assertEquals(HttpStatus.SC_BAD_REQUEST, responseEvent.statusCode)
    }

    @Test
    fun testInvalidFbId(){
        //Invalid facebook id
        val invalidFbIdJson = buildUserProfile("validation/invalidFbId.json",deviceId,sessionId)
        val requestEvent = requestSpecificationWithAuth(invalidFbIdJson,eventToken)
        val responseEvent = requestEvent.post(pushProfileUrl)
        assertEquals(HttpStatus.SC_BAD_REQUEST, responseEvent.statusCode)
    }

    @Test
    fun testInvalidGoogleId(){
        //Invalid google id
        val invalidGoogleIdJson = buildUserProfile("validation/invalidGoogleId.json",deviceId,sessionId)
        val requestEvent = requestSpecificationWithAuth(invalidGoogleIdJson,eventToken)
        val responseEvent = requestEvent.post(pushProfileUrl)
        assertEquals(HttpStatus.SC_BAD_REQUEST, responseEvent.statusCode)
    }

    @Test
    fun testInvalidCountryCode(){
        //Invalid country code
        val invalidCountryCodeJson = buildUserProfile("validation/invalidCountryCode.json",deviceId,sessionId)
        val requestEvent = requestSpecificationWithAuth(invalidCountryCodeJson,eventToken)
        val responseEvent = requestEvent.post(pushProfileUrl)
        assertEquals(HttpStatus.SC_BAD_REQUEST, responseEvent.statusCode)
    }

    /*@Test
    fun testInvalidClientUserId(){
        //Invalid mobile number size
        val invalidClientUserIdJson = buildUserProfile("invalidClientUserId.json",deviceId,sessionId)
        val requestEvent = requestSpecificationWithAuth(invalidClientUserIdJson,eventToken)
        val responseEvent = requestEvent.post(pushProfileUrl)
        assertEquals(HttpStatus.SC_BAD_REQUEST, responseEvent.statusCode)
    }*/

   /* @Test
    fun testInvalidUserId(){
        //Invalid user id
        val invalidUserIdJson = buildUserProfile("invalidUserId.json",deviceId,sessionId)
        val requestEvent = requestSpecificationWithAuth(invalidUserIdJson,eventToken)
        val responseEvent = requestEvent.post(pushProfileUrl)
        assertEquals(HttpStatus.SC_BAD_REQUEST, responseEvent.statusCode)
    }*/

    @Test
    fun testValidEvent(){
        //Valid user event
        val eventJson = buildUserProfile("event1.json",deviceId,sessionId)
        val requestEvent = requestSpecificationWithAuth(eventJson,eventToken)
        val responseEvent = requestEvent.post(pushProfileUrl)
        assertEquals(HttpStatus.SC_OK, responseEvent.statusCode)

        val requestJson = JSONObject(eventJson)
        val request = objectMapper.readValue(eventJson, Event::class.java)
        val response = responseBuilder(request.name)

        assertEquals(request.name, response?.name)
        assertEquals(request.attributes, response?.attributes)
        assertEquals(requestJson.getString("city"), response?.geogrophy?.city)
        assertEquals(requestJson.getString("state"), response?.geogrophy?.state)
        assertEquals(requestJson.getString("country"), response?.geogrophy?.country)
        assertEquals(requestJson.getFloat("latitude"), response?.geoDetails?.geolocation?.coordinate?.latitude)
        assertEquals(requestJson.getFloat("longitude"), response?.geoDetails?.geolocation?.coordinate?.longitude)
        assertEquals(sessionId, response?.sessionId)
        assertEquals(deviceId, response?.deviceId)
    }

    @Test
    fun testValidUserProfile(){
        //Valid user profile
        val userProfileJson = buildUserProfile("userProfile1.json",deviceId,sessionId)
        val requestEvent = requestSpecificationWithAuth(userProfileJson,eventToken)
        val responseEvent = requestEvent.post(pushProfileUrl)
        assertEquals(HttpStatus.SC_OK, responseEvent.statusCode)

        val requestEventUser = objectMapper.readValue(userProfileJson, EventUserWeb::class.java)
        val responseEventUser = responseBuilderUserProfile(requestEventUser.firstName)

        assertEquals(requestEventUser.firstName, responseEventUser?.standardInfo?.firstname)
        assertEquals(requestEventUser.lastName, responseEventUser?.standardInfo?.lastname)
        assertEquals(requestEventUser.email, responseEventUser?.identity?.email)

    }


    private fun mongoDatabase(): MongoDatabase? {
        val mongoClient = MongoClient("192.168.0.109", 27017)
        return mongoClient.getDatabase("eventdb")
    }

    private fun requestSpecificationWithAuth(body: String): RequestSpecification {
        val requestEvent = RestAssured.given()
        requestEvent.body(body)
        requestEvent.contentType(ContentType.JSON)
        requestEvent.header("Authorization", eventToken)
        requestEvent.header("User-Agent", userAgent)
        return requestEvent
    }

    private fun requestSpecificationWithAuth(body: String,token:String): RequestSpecification {
        val requestEvent = RestAssured.given()
        requestEvent.body(body)
        requestEvent.contentType(ContentType.JSON)
        requestEvent.header("Authorization", token)
        requestEvent.header("User-Agent", userAgent)
        return requestEvent
    }

    private fun requestSpecificationWithOutAuth(loginDetails: String): RequestSpecification {
        val request = RestAssured.given()
        request.body(loginDetails)
        request.contentType(ContentType.JSON)
        request.header("User-Agent", userAgent)
        return request
    }

    private fun buildEvent(file: String, deviceId: String?, sessionId: String?, userId: String? = null): String {
        val eventJson = readFileText(file)
        val event = objectMapper.readValue(eventJson, EventWeb::class.java)
        if (sessionId != null) event.identity.sessionId = sessionId
        if (deviceId != null) event.identity.deviceId = deviceId
        event.identity.userId = userId
        return objectMapper.writeValueAsString(event)
    }

    private fun buildUserProfile(file: String, deviceId: String?, sessionId: String?, userId: String? = null): String {
        val eventJson = readFileText(file)
        val eventUser = objectMapper.readValue(eventJson, EventUserWeb::class.java)
        if (sessionId != null) eventUser.identity.sessionId = sessionId
        if (deviceId != null) eventUser.identity.deviceId = deviceId
        eventUser.identity.userId = userId
        return objectMapper.writeValueAsString(eventUser)
    }

    private fun readFileText(fileName: String): String {
        val classLoader = ClassLoader.getSystemClassLoader()
        val file = File(classLoader.getResource(fileName).file)
        return file.readText(Charsets.UTF_8)
    }

    private fun objectMapper(): ObjectMapper {
        val mapper = ObjectMapper()
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, true)
        mapper.registerModule(JavaTimeModule())
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        return mapper
    }

    private fun responseBuilder(name: String?): Event? {
        val db = mongoDatabase()
        val response = db?.let {
            val collection = db.getCollection("34_event")
            val searchQuery = BasicDBObject()
            searchQuery["name"] = name
            val cursor = collection.find(searchQuery)
            val iterableCursor = cursor.iterator()

            var responseJson = ""
            while (iterableCursor.hasNext()) {
                responseJson = iterableCursor.next().toJson(JsonWriterSettings(JsonMode.STRICT))
            }
            collection.deleteOne(searchQuery)
            objectMapper.readValue(responseJson, Event::class.java)
        }
        return response
    }

    private fun responseBuilderUserProfile(firstName: String?): EventUser? {
        val db = mongoDatabase()
        val response = db?.let {
            val collection = db.getCollection("34_eventUser")
            val searchQuery = BasicDBObject()
            searchQuery["standardInfo.firstName"] = firstName
            val cursor = collection.find(searchQuery)
            val iterableCursor = cursor.iterator()

            var responseJson = ""
            while (iterableCursor.hasNext()) {
                responseJson = iterableCursor.next().toJson(JsonWriterSettings(JsonMode.STRICT))
            }
            collection.deleteOne(searchQuery)
            objectMapper.readValue(responseJson, EventUser::class.java)
        }
        return response
    }

}