import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.mongodb.BasicDBObject
import com.mongodb.MongoClient
import com.mongodb.client.MongoDatabase
import com.und.mongo.Event
import com.und.mongo.EventUser
import com.und.web.EventUserWeb
import com.und.web.EventWeb
import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.specification.RequestSpecification
import org.apache.http.HttpStatus
import org.apache.http.protocol.HTTP
import org.bson.json.JsonMode
import org.bson.json.JsonWriterSettings
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.File
import java.io.StringReader
import java.util.*


class IntegrationTestCases {

    //Variable to hold Authorization token
    private var token = ""
    //Variables to hold Device Id, Session Id of the first time user
    private var deviceId = ""
    private var sessionId = ""

    private val userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36"
    private lateinit var objectMapper: ObjectMapper
    private val profile = "dev" //change this to dev to use local urls

    private lateinit var authUrl: String
    private lateinit var eventUrl: String
    private lateinit var pushProfileUrl: String
    private lateinit var eventInitializeUrl: String


    @Before
    fun runBeforeClass() {

        val properties = Properties()
        properties.load(StringReader(readFileText("profiles/$profile.properties")))
        authUrl = properties.getProperty("authUrl")
        eventUrl = properties.getProperty("eventUrl")
        pushProfileUrl = properties.getProperty("pushProfileUrl")
        eventInitializeUrl = properties.getProperty("eventInitializeUrl")
        objectMapper = objectMapper()

        //Generating Token
        val loginDetails = """{"username":"kamalpreet.singh@userndot.com","password":"Kamal123!"}"""
        val request = requestSpecificationWithOutAuth(loginDetails)
        val response = request.post(authUrl)
        token = response.jsonPath().get<String>("data.value.token")

        //Retrieving DeviceId, SessionId, UserId for the first time user
        val eventJson = "{}"
        val requestEvent = requestSpecificationWithAuth(eventJson)
        val responseEvent = requestEvent.post(eventInitializeUrl)
        val responseInit = responseEvent.body
        deviceId = responseEvent.jsonPath().get<String>("data.value.deviceId")
        sessionId = responseEvent.jsonPath().get<String>("data.value.sessionId")

    }


    @Test
    fun eventPushWithAuth() {

        //Testing the correctness of event push and push profile with authorization token and data saved in the database
        //Creating user event anonymously
        val eventJson = buildEvent("event.json", deviceId, sessionId)
        val requestEvent = requestSpecificationWithAuth(eventJson)
        val responseEvent = requestEvent.post(eventUrl)

        assertEquals(HttpStatus.SC_OK, responseEvent.statusCode)

        //Creating another user event anonymously
        val eventJson2 = buildEvent("event2.json", deviceId, sessionId)
        val requestEvent2 = requestSpecificationWithAuth(eventJson2)
        val responseEvent2 = requestEvent2.post(eventUrl)

        assertEquals(HttpStatus.SC_OK, responseEvent2.statusCode)

        //Creating user profile
        val userProfileJson = buildUserProfile("userprofile.json", deviceId, sessionId)
        val requestProfile = requestSpecificationWithAuth(userProfileJson)
        val responseProfile = requestProfile.post(pushProfileUrl)

        assertEquals(HttpStatus.SC_OK, responseProfile.statusCode)
        val userId = responseProfile.jsonPath().get<String>("data.value.userId")

        //Creating event after creating user profile
        val eventJson3 = buildEvent("event3.json", deviceId, sessionId, userId)
        val requestEvent3 = requestSpecificationWithAuth(eventJson3)
        val responseEvent3 = requestEvent3.post(eventUrl)

        assertEquals(HttpStatus.SC_OK, responseEvent3.statusCode)

        //Updating user profile
        val userProfileUpdatedJson = buildUserProfile("userprofileupdate.json", deviceId, sessionId, userId)
        val requestProfileUpdated = requestSpecificationWithAuth(userProfileUpdatedJson)
        val responseProfileUpdated = requestProfileUpdated.post(pushProfileUrl)

        assertEquals(HttpStatus.SC_OK, responseProfileUpdated.statusCode)

        //Updating user profile
        val userProfileUpdatedJson2 = buildUserProfile("userprofileupdate2.json", deviceId, sessionId, userId)
        val requestProfileUpdated2 = requestSpecificationWithAuth(userProfileUpdatedJson2)
        val responseProfileUpdated2 = requestProfileUpdated2.post(pushProfileUrl)

        assertEquals(HttpStatus.SC_OK, responseProfileUpdated2.statusCode)

        //Creating event after updating user profile
        val eventJson4 = buildEvent("event4.json", deviceId, sessionId, userId)
        val requestEvent4 = requestSpecificationWithAuth(eventJson4)
        val responseEvent4 = requestEvent4.post(eventUrl)

        assertEquals(HttpStatus.SC_OK, responseEvent4.statusCode)

        //Checking event data in MongoDB Database

        val requestJson = JSONObject(eventJson).toString()
        val requestEventMongo = objectMapper.readValue(requestJson, Event::class.java)
        val requestEventWeb=objectMapper.readValue(requestJson, EventWeb::class.java)
        val response = responseBuilder(requestEventMongo)

        assertEquals(requestEventWeb.name, response?.name)
        assertEquals(requestEventWeb.attributes.getValue("item"), response?.attributes?.getValue("item"))
        assertEquals(requestEventWeb.attributes.getValue("company"), response?.attributes?.getValue("company"))
        assertEquals(requestEventWeb.attributes.getValue("model"), response?.attributes?.getValue("model"))
        assertEquals(requestEventWeb.attributes.getValue("colour"), response?.attributes?.getValue("colour"))
        assertEquals(requestEventWeb.city, response?.geogrophy?.city)
        assertEquals(requestEventWeb.state, response?.geogrophy?.state)
        assertEquals(requestEventWeb.country, response?.geogrophy?.country)


        val requestJson2 = JSONObject(eventJson2).toString()
        val requestEventMongo2 = objectMapper.readValue(requestJson2, Event::class.java)
        val requestEventWeb2=objectMapper.readValue(requestJson2, EventWeb::class.java)
        val response2 = responseBuilder(requestEventMongo2)

        assertEquals(requestEventWeb2.name, response2?.name)
        assertEquals(requestEventWeb2.attributes.getValue("item"), response2?.attributes?.getValue("item"))
        assertEquals(requestEventWeb2.attributes.getValue("company"), response2?.attributes?.getValue("company"))
        assertEquals(requestEventWeb2.attributes.getValue("model"), response2?.attributes?.getValue("model"))
        assertEquals(requestEventWeb2.attributes.getValue("colour"), response2?.attributes?.getValue("colour"))
        assertEquals(requestEventWeb2.city, response2?.geogrophy?.city)
        assertEquals(requestEventWeb2.state, response2?.geogrophy?.state)
        assertEquals(requestEventWeb2.country, response2?.geogrophy?.country)

        val requestJson3 = JSONObject(eventJson3).toString()
        val requestEventMongo3 = objectMapper.readValue(requestJson3, Event::class.java)
        val requestEventWeb3=objectMapper.readValue(requestJson3, EventWeb::class.java)
        val response3 = responseBuilder(requestEventMongo3)

        assertEquals(requestEventWeb3.name, response3?.name)
        assertEquals(requestEventWeb3.attributes.getValue("item"), response3?.attributes?.getValue("item"))
        assertEquals(requestEventWeb3.attributes.getValue("company"), response3?.attributes?.getValue("company"))
        assertEquals(requestEventWeb3.attributes.getValue("model"), response3?.attributes?.getValue("model"))
        assertEquals(requestEventWeb3.attributes.getValue("colour"), response3?.attributes?.getValue("colour"))
        assertEquals(requestEventWeb3.city, response3?.geogrophy?.city)
        assertEquals(requestEventWeb3.state, response3?.geogrophy?.state)
        assertEquals(requestEventWeb3.country, response3?.geogrophy?.country)

        val requestJson4 = JSONObject(eventJson4).toString()
        val requestEventMongo4 = objectMapper.readValue(requestJson4, Event::class.java)
        val requestEventWeb4=objectMapper.readValue(requestJson4, EventWeb::class.java)
        val response4 = responseBuilder(requestEventMongo4)

        assertEquals(requestEventWeb4.name, response4?.name)
        assertEquals(requestEventWeb4.attributes.getValue("item"), response4?.attributes?.getValue("item"))
        assertEquals(requestEventWeb4.attributes.getValue("company"), response4?.attributes?.getValue("company"))
        assertEquals(requestEventWeb4.attributes.getValue("model"), response4?.attributes?.getValue("model"))
        assertEquals(requestEventWeb4.attributes.getValue("colour"), response4?.attributes?.getValue("colour"))
        assertEquals(requestEventWeb4.city, response4?.geogrophy?.city)
        assertEquals(requestEventWeb4.state, response4?.geogrophy?.state)
        assertEquals(requestEventWeb4.country, response4?.geogrophy?.country)

        val requestEventUserJson = JSONObject(userProfileJson).toString()
        val requestEventUserMongo = objectMapper.readValue(requestEventUserJson, EventUser::class.java)
        val requestEventUserWeb = objectMapper.readValue(requestEventUserJson, EventUserWeb::class.java)
        val responseEventUser = responseBuilderUserProfile(requestEventUserMongo)

        assertEquals(requestEventUserWeb.firstName, responseEventUser?.standardInfo?.firstName)
        assertEquals(requestEventUserWeb.lastName, responseEventUser?.standardInfo?.lastName)
        assertEquals(requestEventUserWeb.email, responseEventUser?.socialId?.email)

        val requestEventUserJson2 = JSONObject(userProfileUpdatedJson).toString()
        //val requestEventUserMongo2 = objectMapper.readValue(requestEventUserJson2, EventUser::class.java)
        val requestEventUserWeb2 = objectMapper.readValue(requestEventUserJson2, EventUserWeb::class.java)
        //val responseEventUser2 = responseBuilderUserProfile(requestEventUserMongo2)

        assertEquals(requestEventUserWeb2.mobile, responseEventUser?.socialId?.mobile)
        assertEquals(requestEventUserWeb2.countryCode, responseEventUser?.standardInfo?.countryCode)
        assertEquals(requestEventUserWeb2.fbId, responseEventUser?.socialId?.fbId)

        val requestEventUserJson3 = JSONObject(userProfileUpdatedJson2).toString()
        //val requestEventUserMongo3 = objectMapper.readValue(requestEventUserJson3, EventUser::class.java)
        val requestEventUserWeb3 = objectMapper.readValue(requestEventUserJson3, EventUserWeb::class.java)
        //val responseEventUser3 = responseBuilderUserProfile(requestEventUserMongo3)

        assertEquals(requestEventUserWeb3.dob, responseEventUser?.standardInfo?.dob)
        assertEquals(requestEventUserWeb3.country, responseEventUser?.standardInfo?.country)


    }

    private fun mongoDatabase(): MongoDatabase? {
        val mongoClient = MongoClient("192.168.0.109", 27017)
        return mongoClient.getDatabase("eventdb")
    }

    private fun requestSpecificationWithAuth(body: String): RequestSpecification {
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
        request.header(HTTP.CONTENT_TYPE, ContentType.JSON)
        request.header("User-Agent", userAgent)
        return request
    }

    private fun buildEvent(file: String, deviceid: String?, sessionId: String?, userId: String? = null): String {
        val eventJson = readFileText(file)
        val event = objectMapper.readValue(eventJson, EventWeb::class.java)
        if (sessionId != null) event.identity.sessionId = sessionId
        if (deviceid != null) event.identity.deviceId = deviceid
        event.identity.userId = userId
        return objectMapper.writeValueAsString(event)
    }

    private fun buildUserProfile(file: String, deviceid: String?, sessionId: String?, userId: String? = null): String {
        val eventJson = readFileText(file)
        val eventUser = objectMapper.readValue(eventJson, EventUserWeb::class.java)
        if (sessionId != null) eventUser.identity.sessionId = sessionId
        if (deviceid != null) eventUser.identity.deviceId = deviceid
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

    private fun responseBuilder(request: Event): Event? {
        val db = mongoDatabase()
        val response = db?.let {
            val collection = db.getCollection("13_event")
            val searchQuery = BasicDBObject()
            searchQuery["name"] = request.name
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

    private fun responseBuilderUserProfile(request: EventUser): EventUser? {
        val db = mongoDatabase()
        val response = db?.let {
            val collection = db.getCollection("13_eventUser")
            val searchQuery = BasicDBObject()
            searchQuery["firstName"] = request.standardInfo.firstName
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


