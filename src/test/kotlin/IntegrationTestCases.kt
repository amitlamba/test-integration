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
    lateinit var objectMapper: ObjectMapper

    private val profile = "server" //change this to dev to use local urls

    private lateinit var authUrl: String
    private lateinit var eventUrl: String
    private lateinit var pushProfileUrl: String
    private lateinit var eventInitializeUrl: String


    @Before
    fun runBeforeClass() {
        val properties = Properties()
        properties.load(StringReader(readFileText("profiles/${profile}.properties")))
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
        val eventJson = buildevent("event.json", deviceId, sessionId)
        val requestEvent = requestSpecificationWithAuth(eventJson)
        val responseEvent = requestEvent.post(eventUrl)

        assertEquals(HttpStatus.SC_OK, responseEvent.statusCode)

        //Creating another user event anonymously
        val eventJson2 = buildevent("event2.json", deviceId, sessionId)
        val requestEvent2 = requestSpecificationWithAuth(eventJson2)
        val responseEvent2 = requestEvent2.post(eventUrl)

        assertEquals(HttpStatus.SC_OK, responseEvent2.statusCode)

        //Creating user profile
        val userProfileJson = builUserProfile("userprofile.json", deviceId, sessionId)
        val requestProfile = requestSpecificationWithAuth(userProfileJson)
        val responseProfile = requestProfile.post(pushProfileUrl)

        assertEquals(HttpStatus.SC_OK, responseProfile.statusCode)
        val userId = responseProfile.jsonPath().get<String>("data.value.userId")

        //Creating event after creating user profile
        val eventJson3 = buildevent("event3.json", deviceId, sessionId, userId)
        val requestEvent3 = requestSpecificationWithAuth(eventJson3)
        val responseEvent3 = requestEvent3.post(eventUrl)

        assertEquals(HttpStatus.SC_OK, responseEvent3.statusCode)

        //Updating user profile
        val userProfileUpdatedJson = builUserProfile("userprofileupdate.json", deviceId, sessionId)
        val requestProfileUpdated = requestSpecificationWithAuth(userProfileUpdatedJson)
        val responseProfileUpdated = requestProfileUpdated.post(pushProfileUrl)

        assertEquals(HttpStatus.SC_OK, responseProfileUpdated.statusCode)

        //Updating user profile
        val userProfileUpdatedJson2 = builUserProfile("userprofileupdate2.json", deviceId, sessionId)
        val requestProfileUpdated2 = requestSpecificationWithAuth(userProfileUpdatedJson2)
        val responseProfileUpdated2 = requestProfileUpdated2.post(pushProfileUrl)

        assertEquals(HttpStatus.SC_OK, responseProfileUpdated2.statusCode)

        //Creating event after updating user profile
        val eventJson4 = buildevent("event4.json", deviceId, sessionId, userId)
        val requestEvent4 = requestSpecificationWithAuth(eventJson4)
        val responseEvent4 = requestEvent4.post(eventUrl)

        assertEquals(HttpStatus.SC_OK, responseEvent4.statusCode)

        //Checking event data in MongoDB Database

        val requestJson = JSONObject(eventJson).toString()
        val request = objectMapper.readValue(requestJson, Event::class.java)

        val db = mongoDatabase()
        val response = db?.let {
            val collection = db.getCollection("13_event")
            val searchQuery = BasicDBObject()
            searchQuery["name"] = request?.name
            val cursor = collection.find(searchQuery)
            val iterableCursor = cursor.iterator()

            var responseJson = ""
            while (iterableCursor.hasNext()) {
                responseJson = JSONObject(iterableCursor.next()).toString()
            }
            objectMapper.readValue(responseJson, Event::class.java)
        }
        assertEquals(request?.name, response?.name)
        assertEquals(request?.geogrophy, response?.geogrophy)
        assertEquals(request?.deviceId, response?.deviceId)


        /*lateinit var jsonResponse:Document
        while (iterableCursor.hasNext()){
            jsonResponse=iterableCursor.next()
        }
        println(jsonResponse)*/

        //var nameResponse=jsonResponse.getString("name")
        //var geogrophy=jsonResponse.getJSONObject("geogrophy")
        // var cityResponse=jsonResponse.getString("geogrophy.city")
        /*var stateResponse=geogrophy.getString("state")
        var countryResponse=geogrophy.getString("country")*/

        /*assertEquals(name,nameResponse)
        assertEquals(city,cityResponse)
        assertEquals(state,stateResponse)
        assertEquals(country,countryResponse)*/
        //collection.deleteOne(eq("name",name))


        /* searchQuery["name"]="chargedSportsCar"
         var cursor = collection.find(searchQuery)
         for (doc in cursor) {
             println(doc)
         }
         collection.deleteOne(searchQuery)

         searchQuery["name"]="chargedItem"
         cursor = collection.find(searchQuery)
         for (doc in cursor) {
             println(doc)
         }
         collection.deleteOne(searchQuery)

         searchQuery["name"]="AddedToWishList"
         cursor = collection.find(searchQuery)
         for (doc in cursor) {
             println(doc)
         }
         collection.deleteOne(searchQuery)

         //Checking user profile data in MongoDB Database
         val collectionUserProfile = db.getCollection("13_eventuser")
         val searchQueryUserProfile = BasicDBObject()
         searchQueryUserProfile["firstName"] = "Kamalpreet"
         val cursorUserProfile = collectionUserProfile.find(searchQueryUserProfile)
         for (doc in cursorUserProfile) {
             println(doc)
         }
         collection.deleteOne(searchQueryUserProfile)*/

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

    private fun buildevent(file: String, deviceid: String?, sessionId: String?, userId: String? = null): String {
        val eventJson = readFileText(file)
        val event = objectMapper.readValue(eventJson, EventWeb::class.java)
        if (sessionId != null) event.identity.sessionId = sessionId
        if (deviceid != null) event.identity.deviceId = deviceid
        event.identity.userId = userId
        return objectMapper.writeValueAsString(event)
    }

    private fun builUserProfile(file: String, deviceid: String?, sessionId: String?, userId: String? = null): String {
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
}


