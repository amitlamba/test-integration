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
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import java.io.File
import java.io.StringReader
import java.util.*


class IntegrationTestCasesEventAPI {

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
        authEventUrl = properties.getProperty("authEventUrl")
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
        val eventTokenJson = "{}"
        val requestEventToken = requestSpecificationWithAuth(eventTokenJson, token)
        val responseEventToken = requestEventToken.post(authEventUrl)
        eventToken = responseEventToken.jsonPath().get<String>("data.value.token")

        //Retrieving DeviceId, SessionId, UserId for the first time user
        val eventJson = "{}"
        val requestEvent = requestSpecificationWithAuth(eventJson)
        val responseEvent = requestEvent.post(eventInitializeUrl)

        deviceId = responseEvent.jsonPath().get<String>("data.value.deviceId")
        sessionId = responseEvent.jsonPath().get<String>("data.value.sessionId")

    }


    @Test
    fun testFlowOfEvents1() {
        //Testing the correctness of event push and push profile with authorization token and data saved in the database

        //Viewed Products anonymously
        val eventJson = buildEvent("event1.json", deviceId, sessionId)
        val requestEvent = requestSpecificationWithAuth(eventJson)
        val responseEvent = requestEvent.post(eventUrl)
        assertEquals(HttpStatus.SC_OK, responseEvent.statusCode)

        //Viewed more products anonymously
        val eventJson2 = buildEvent("event2.json", deviceId, sessionId)
        val requestEvent2 = requestSpecificationWithAuth(eventJson2)
        val responseEvent2 = requestEvent2.post(eventUrl)
        assertEquals(HttpStatus.SC_OK, responseEvent2.statusCode)

        //Created user profile
        val userProfileJson = buildUserProfile("userProfile1.json", deviceId, sessionId)
        val requestProfile = requestSpecificationWithAuth(userProfileJson)
        val responseProfile = requestProfile.post(pushProfileUrl)
        assertEquals(HttpStatus.SC_OK, responseProfile.statusCode)
        val userId = responseProfile.jsonPath().get<String>("data.value.userId")

        //Product added to wish list in user profile
        val eventJson3 = buildEvent("event3.json", deviceId, sessionId, userId)
        val requestEvent3 = requestSpecificationWithAuth(eventJson3)
        val responseEvent3 = requestEvent3.post(eventUrl)
        assertEquals(HttpStatus.SC_OK, responseEvent3.statusCode)

        //Updated user profile
        val userProfileUpdatedJson = buildUserProfile("userProfile1Updated1.json", deviceId, sessionId, userId)
        val requestProfileUpdated = requestSpecificationWithAuth(userProfileUpdatedJson)
        val responseProfileUpdated = requestProfileUpdated.post(pushProfileUrl)
        assertEquals(HttpStatus.SC_OK, responseProfileUpdated.statusCode)

        //again updated user profile
        val userProfileUpdatedJson2 = buildUserProfile("userProfile1Updated2.json", deviceId, sessionId, userId)
        val requestProfileUpdated2 = requestSpecificationWithAuth(userProfileUpdatedJson2)
        val responseProfileUpdated2 = requestProfileUpdated2.post(pushProfileUrl)
        assertEquals(HttpStatus.SC_OK, responseProfileUpdated2.statusCode)

        //purchased products
        val eventJson4 = buildEvent("event4.json", deviceId, sessionId, userId)
        val requestEvent4 = requestSpecificationWithAuth(eventJson4)
        val responseEvent4 = requestEvent4.post(eventUrl)
        assertEquals(HttpStatus.SC_OK, responseEvent4.statusCode)


        //Checking event data in MongoDB Database

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
        assertEquals(userId, response?.userId)
        assertEquals(sessionId, response?.sessionId)
        assertEquals(deviceId, response?.deviceId)

        val requestJson2 = JSONObject(eventJson2)
        val request2 = objectMapper.readValue(eventJson2, Event::class.java)
        val response2 = responseBuilder(request2.name)

        assertEquals(request2.name, response2?.name)
        assertEquals(request2.attributes, response2?.attributes)
        assertEquals(requestJson2.getString("city"), response2?.geogrophy?.city)
        assertEquals(requestJson2.getString("state"), response2?.geogrophy?.state)
        assertEquals(requestJson2.getString("country"), response2?.geogrophy?.country)
        assertEquals(requestJson2.getFloat("latitude"), response2?.geoDetails?.geolocation?.coordinate?.latitude)
        assertEquals(requestJson2.getFloat("longitude"), response2?.geoDetails?.geolocation?.coordinate?.longitude)
        assertEquals(userId, response2?.userId)
        assertEquals(sessionId, response2?.sessionId)
        assertEquals(deviceId, response2?.deviceId)

        val requestJson3 = JSONObject(eventJson3)
        val request3 = objectMapper.readValue(eventJson3, Event::class.java)
        val response3 = responseBuilder(request3.name)

        assertEquals(request3.name, response3?.name)
        assertEquals(request3.attributes, response3?.attributes)
        assertEquals(requestJson3.getString("city"), response3?.geogrophy?.city)
        assertEquals(requestJson3.getString("state"), response3?.geogrophy?.state)
        assertEquals(requestJson3.getString("country"), response3?.geogrophy?.country)
        assertEquals(userId, response3?.userId)
        assertEquals(sessionId, response3?.sessionId)
        assertEquals(deviceId, response3?.deviceId)

        val requestJson4 = JSONObject(eventJson4)
        val request4 = objectMapper.readValue(eventJson4, Event::class.java)
        val response4 = responseBuilder(request4.name)

        assertEquals(request4.name, response4?.name)
        assertEquals(request4.attributes, response4?.attributes)
        assertEquals(requestJson4.getString("city"), response4?.geogrophy?.city)
        assertEquals(requestJson4.getString("state"), response4?.geogrophy?.state)
        assertEquals(requestJson4.getString("country"), response4?.geogrophy?.country)
        assertEquals(request4.lineItem[0].categories, response4!!.lineItem[0].categories)
        assertEquals(request4.lineItem[0].currency, response4.lineItem[0].currency)
        assertEquals(request4.lineItem[0].price, response4.lineItem[0].price)
        assertEquals(request4.lineItem[0].product, response4.lineItem[0].product)
        assertEquals(request4.lineItem[0].quantity, response4.lineItem[0].quantity)
        assertEquals(userId, response4.userId)
        assertEquals(sessionId, response4.sessionId)
        assertEquals(deviceId, response4.deviceId)

        val requestEventUser = objectMapper.readValue(userProfileJson, EventUserWeb::class.java)
        val responseEventUser = responseBuilderUserProfile(requestEventUser.firstName)

        assertEquals(requestEventUser.firstName, responseEventUser?.standardInfo?.firstname)
        assertEquals(requestEventUser.lastName, responseEventUser?.standardInfo?.lastname)
        assertEquals(requestEventUser.email, responseEventUser?.identity?.email)
        assertNotEquals(requestEventUser.fbId, responseEventUser?.identity?.fbId)

        val requestEventUser2 = objectMapper.readValue(userProfileUpdatedJson, EventUserWeb::class.java)

        assertEquals(requestEventUser2.mobile, responseEventUser?.identity?.mobile)
        assertEquals(requestEventUser2.fbId, responseEventUser?.identity?.fbId)
        assertEquals(requestEventUser2.googleId, responseEventUser?.identity?.googleId)
        assertEquals(requestEventUser2.gender, responseEventUser?.standardInfo?.gender)

        val requestEventUser3 = objectMapper.readValue(userProfileUpdatedJson2, EventUserWeb::class.java)

        assertEquals(requestEventUser3.dob, responseEventUser?.standardInfo?.dob)
        assertEquals(requestEventUser3.country, responseEventUser?.standardInfo?.country)
        assertEquals(requestEventUser3.additionalInfo, responseEventUser?.additionalInfo)

    }

    @Test
    fun testFlowOfEvents2() {

        //Searched Products anonymously
        val eventJson = buildEvent("event5.json", deviceId, sessionId)
        val requestEvent = requestSpecificationWithAuth(eventJson)
        val responseEvent = requestEvent.post(eventUrl)
        assertEquals(HttpStatus.SC_OK, responseEvent.statusCode)

        //Created user profile
        val userProfileJson = buildUserProfile("userProfile2.json", deviceId, sessionId)
        val requestProfile = requestSpecificationWithAuth(userProfileJson)
        val responseProfile = requestProfile.post(pushProfileUrl)
        assertEquals(HttpStatus.SC_OK, responseProfile.statusCode)
        val userId = responseProfile.jsonPath().get<String>("data.value.userId")

        //Products added to cart
        val eventJson2 = buildEvent("event6.json", deviceId, sessionId, userId)
        val requestEvent2 = requestSpecificationWithAuth(eventJson2)
        val responseEvent2 = requestEvent2.post(eventUrl)
        assertEquals(HttpStatus.SC_OK, responseEvent2.statusCode)

        //Products purchased
        val eventJson3 = buildEvent("event7.json", deviceId, sessionId, userId)
        val requestEvent3 = requestSpecificationWithAuth(eventJson3)
        val responseEvent3 = requestEvent3.post(eventUrl)
        assertEquals(HttpStatus.SC_OK, responseEvent3.statusCode)

        //Checking data in Mongo Database Collection

        val requestJson = JSONObject(eventJson)
        val request = objectMapper.readValue(eventJson, Event::class.java)
        val response = responseBuilder(request.name)

        assertEquals(request.name, response?.name)
        assertEquals(request.attributes, response?.attributes)
        assertEquals(requestJson.getString("city"), response?.geogrophy?.city)
        assertEquals(requestJson.getString("state"), response?.geogrophy?.state)
        assertEquals(requestJson.getString("country"), response?.geogrophy?.country)
        assertEquals(userId, response?.userId)
        assertEquals(sessionId, response?.sessionId)
        assertEquals(deviceId, response?.deviceId)

        val requestJson2 = JSONObject(eventJson2)
        val request2 = objectMapper.readValue(eventJson2, Event::class.java)
        val response2 = responseBuilder(request2.name)

        assertEquals(request2.name, response2?.name)
        assertEquals(request2.attributes, response2?.attributes)
        assertEquals(requestJson2.getString("city"), response2?.geogrophy?.city)
        assertEquals(requestJson2.getString("state"), response2?.geogrophy?.state)
        assertEquals(requestJson2.getString("country"), response2?.geogrophy?.country)
        assertEquals(requestJson2.getFloat("latitude"), response2?.geoDetails?.geolocation?.coordinate?.latitude)
        assertEquals(requestJson2.getFloat("longitude"), response2?.geoDetails?.geolocation?.coordinate?.longitude)
        assertEquals(userId, response2?.userId)
        assertEquals(sessionId, response2?.sessionId)
        assertEquals(deviceId, response2?.deviceId)

        val requestJson3 = JSONObject(eventJson3)
        val request3 = objectMapper.readValue(eventJson3, Event::class.java)
        val response3 = responseBuilder(request3.name)

        assertEquals(request3.name, response3?.name)
        assertEquals(request3.attributes, response3?.attributes)
        assertEquals(requestJson3.getString("city"), response3?.geogrophy?.city)
        assertEquals(requestJson3.getString("state"), response3?.geogrophy?.state)
        assertEquals(requestJson3.getString("country"), response3?.geogrophy?.country)
        assertEquals(request3.lineItem[0].categories, response3!!.lineItem[0].categories)
        assertEquals(request3.lineItem[0].currency, response3.lineItem[0].currency)
        assertEquals(request3.lineItem[0].price, response3.lineItem[0].price)
        assertEquals(request3.lineItem[0].product, response3.lineItem[0].product)
        assertEquals(request3.lineItem[0].quantity, response3.lineItem[0].quantity)
        assertEquals(userId, response3.userId)
        assertEquals(sessionId, response3.sessionId)
        assertEquals(deviceId, response3.deviceId)

        val requestEventUser = objectMapper.readValue(userProfileJson, EventUserWeb::class.java)
        val responseEventUser = responseBuilderUserProfile(requestEventUser.firstName)

        assertEquals(requestEventUser.firstName, responseEventUser?.standardInfo?.firstname)
        assertEquals(requestEventUser.lastName, responseEventUser?.standardInfo?.lastname)
        assertEquals(requestEventUser.mobile, responseEventUser?.identity?.mobile)
        assertEquals(requestEventUser.dob, responseEventUser?.standardInfo?.dob)
        assertEquals(requestEventUser.email, responseEventUser?.identity?.email)
        assertEquals(requestEventUser.gender, responseEventUser?.standardInfo?.gender)
        assertEquals(requestEventUser.additionalInfo, responseEventUser?.additionalInfo)

    }

    @Test
    fun testFlowOfEvents3() {

        //Created user profile
        val userProfileJson = buildUserProfile("userProfile3.json", deviceId, sessionId)
        val requestProfile = requestSpecificationWithAuth(userProfileJson)
        val responseProfile = requestProfile.post(pushProfileUrl)
        assertEquals(HttpStatus.SC_OK, responseProfile.statusCode)
        val userId = responseProfile.jsonPath().get<String>("data.value.userId")

        //Viewed products
        val eventJson = buildEvent("event8.json", deviceId, sessionId,userId)
        val requestEvent = requestSpecificationWithAuth(eventJson)
        val responseEvent = requestEvent.post(eventUrl)
        assertEquals(HttpStatus.SC_OK, responseEvent.statusCode)

        //Updated user profile
        val userProfileUpdatedJson = buildUserProfile("userProfile3Updated1.json", deviceId, sessionId, userId)
        val requestProfileUpdated = requestSpecificationWithAuth(userProfileUpdatedJson)
        val responseProfileUpdated = requestProfileUpdated.post(pushProfileUrl)
        assertEquals(HttpStatus.SC_OK, responseProfileUpdated.statusCode)

        //Purchased Products
        val eventJson2 = buildEvent("event10.json", deviceId, sessionId,userId)
        val requestEvent2 = requestSpecificationWithAuth(eventJson2)
        val responseEvent2 = requestEvent2.post(eventUrl)
        assertEquals(HttpStatus.SC_OK, responseEvent2.statusCode)

        //Checking in Mongo Database Collection

        val requestJson = JSONObject(eventJson)
        val request = objectMapper.readValue(eventJson, Event::class.java)
        val response = responseBuilder(request.name)

        assertEquals(request.name, response?.name)
        assertEquals(request.attributes, response?.attributes)
        assertEquals(requestJson.getString("city"), response?.geogrophy?.city)
        assertEquals(requestJson.getString("state"), response?.geogrophy?.state)
        assertEquals(requestJson.getString("country"), response?.geogrophy?.country)
        assertEquals(userId, response?.userId)
        assertEquals(sessionId, response?.sessionId)
        assertEquals(deviceId, response?.deviceId)

        val requestJson2 = JSONObject(eventJson2)
        val request2 = objectMapper.readValue(eventJson2, Event::class.java)
        val response2 = responseBuilder(request2.name)

        assertEquals(request2.name, response2?.name)
        assertEquals(request2.attributes, response2?.attributes)
        assertEquals(requestJson2.getString("city"), response2?.geogrophy?.city)
        assertEquals(requestJson2.getString("state"), response2?.geogrophy?.state)
        assertEquals(requestJson2.getString("country"), response2?.geogrophy?.country)
        assertEquals(requestJson2.getFloat("latitude"), response2?.geoDetails?.geolocation?.coordinate?.latitude)
        assertEquals(requestJson2.getFloat("longitude"), response2?.geoDetails?.geolocation?.coordinate?.longitude)
        assertEquals(request2.lineItem[0].categories, response2!!.lineItem[0].categories)
        assertEquals(request2.lineItem[0].currency, response2.lineItem[0].currency)
        assertEquals(request2.lineItem[0].price, response2.lineItem[0].price)
        assertEquals(request2.lineItem[0].product, response2.lineItem[0].product)
        assertEquals(request2.lineItem[0].quantity, response2.lineItem[0].quantity)
        assertEquals(request2.lineItem[0].properties, response2.lineItem[0].properties)
        assertEquals(userId, response2.userId)
        assertEquals(sessionId, response2.sessionId)
        assertEquals(deviceId, response2.deviceId)


        val requestEventUser = objectMapper.readValue(userProfileJson, EventUserWeb::class.java)
        val responseEventUser = responseBuilderUserProfile(requestEventUser.firstName)

        assertEquals(requestEventUser.firstName, responseEventUser?.standardInfo?.firstname)
        assertEquals(requestEventUser.lastName, responseEventUser?.standardInfo?.lastname)
        assertEquals(requestEventUser.mobile, responseEventUser?.identity?.mobile)
        assertEquals(requestEventUser.email, responseEventUser?.identity?.email)
        assertEquals(requestEventUser.dob, responseEventUser?.standardInfo?.dob)
        assertEquals(requestEventUser.gender, responseEventUser?.standardInfo?.gender)
        assertEquals(requestEventUser.country, responseEventUser?.standardInfo?.country)

        val requestEventUser2 = objectMapper.readValue(userProfileUpdatedJson, EventUserWeb::class.java)
        assertEquals(requestEventUser2.fbId, responseEventUser?.identity?.fbId)
        assertEquals(requestEventUser2.googleId, responseEventUser?.identity?.googleId)
        assertEquals(requestEventUser2.additionalInfo, responseEventUser?.additionalInfo)

    }

    @Test
    fun testFlowOfEvents4() {

        //Viewed products anonymously
        val eventJson = buildEvent("event1.json", deviceId, sessionId)
        val requestEvent = requestSpecificationWithAuth(eventJson)
        val responseEvent = requestEvent.post(eventUrl)
        assertEquals(HttpStatus.SC_OK, responseEvent.statusCode)

        //Created user profile
        val userProfileJson = buildUserProfile("userProfile1.json", deviceId, sessionId)
        val requestProfile = requestSpecificationWithAuth(userProfileJson)
        val responseProfile = requestProfile.post(pushProfileUrl)
        assertEquals(HttpStatus.SC_OK, responseProfile.statusCode)
        val userId = responseProfile.jsonPath().get<String>("data.value.userId")

        //Added to wish list
        val eventJson2 = buildEvent("event3.json", deviceId, sessionId,userId)
        val requestEvent2 = requestSpecificationWithAuth(eventJson2)
        val responseEvent2 = requestEvent2.post(eventUrl)
        assertEquals(HttpStatus.SC_OK, responseEvent2.statusCode)

        //Added to cart
        val eventJson3 = buildEvent("event6.json", deviceId, sessionId,userId)
        val requestEvent3 = requestSpecificationWithAuth(eventJson3)
        val responseEvent3 = requestEvent3.post(eventUrl)
        assertEquals(HttpStatus.SC_OK, responseEvent3.statusCode)

        //Promotion applied
        val eventJson4 = buildEvent("event11.json", deviceId, sessionId,userId)
        val requestEvent4 = requestSpecificationWithAuth(eventJson4)
        val responseEvent4 = requestEvent4.post(eventUrl)
        assertEquals(HttpStatus.SC_OK, responseEvent4.statusCode)

        //Purchased products
        val eventJson5 = buildEvent("event10.json", deviceId, sessionId,userId)
        val requestEvent5 = requestSpecificationWithAuth(eventJson5)
        val responseEvent5 = requestEvent5.post(eventUrl)
        assertEquals(HttpStatus.SC_OK, responseEvent5.statusCode)

        //Updated user profile
        val userProfileUpdatedJson = buildUserProfile("userProfile1Updated1.json", deviceId, sessionId, userId)
        val requestProfileUpdated = requestSpecificationWithAuth(userProfileUpdatedJson)
        val responseProfileUpdated = requestProfileUpdated.post(pushProfileUrl)
        assertEquals(HttpStatus.SC_OK, responseProfileUpdated.statusCode)

        //Checking in Mongo Database Collection

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
        assertEquals(userId, response?.userId)
        assertEquals(sessionId, response?.sessionId)
        assertEquals(deviceId, response?.deviceId)

        val requestJson2 = JSONObject(eventJson2)
        val request2 = objectMapper.readValue(eventJson2, Event::class.java)
        val response2 = responseBuilder(request2.name)

        assertEquals(request2.name, response2?.name)
        assertEquals(request2.attributes, response2?.attributes)
        assertEquals(requestJson2.getString("city"), response2?.geogrophy?.city)
        assertEquals(requestJson2.getString("state"), response2?.geogrophy?.state)
        assertEquals(requestJson2.getString("country"), response2?.geogrophy?.country)
        assertEquals(userId, response2?.userId)
        assertEquals(sessionId, response2?.sessionId)
        assertEquals(deviceId, response2?.deviceId)

        val requestJson3 = JSONObject(eventJson3)
        val request3 = objectMapper.readValue(eventJson3, Event::class.java)
        val response3 = responseBuilder(request3.name)

        assertEquals(request3.name, response3?.name)
        assertEquals(request3.attributes, response3?.attributes)
        assertEquals(requestJson3.getString("city"), response3?.geogrophy?.city)
        assertEquals(requestJson3.getString("state"), response3?.geogrophy?.state)
        assertEquals(requestJson3.getString("country"), response3?.geogrophy?.country)
        assertEquals(requestJson3.getFloat("latitude"), response3?.geoDetails?.geolocation?.coordinate?.latitude)
        assertEquals(requestJson3.getFloat("longitude"), response3?.geoDetails?.geolocation?.coordinate?.longitude)
        assertEquals(userId, response3?.userId)
        assertEquals(sessionId, response3?.sessionId)
        assertEquals(deviceId, response3?.deviceId)

        val requestJson4 = JSONObject(eventJson4)
        val request4 = objectMapper.readValue(eventJson4, Event::class.java)
        val response4 = responseBuilder(request4.name)

        assertEquals(request4.name, response4?.name)
        assertEquals(request4.attributes, response4?.attributes)
        assertEquals(requestJson4.getString("city"), response4?.geogrophy?.city)
        assertEquals(requestJson4.getString("state"), response4?.geogrophy?.state)
        assertEquals(requestJson4.getString("country"), response4?.geogrophy?.country)
        assertEquals(userId, response4?.userId)
        assertEquals(sessionId, response4?.sessionId)
        assertEquals(deviceId, response4?.deviceId)

        val requestJson5 = JSONObject(eventJson5)
        val request5 = objectMapper.readValue(eventJson5, Event::class.java)
        val response5 = responseBuilder(request5.name)

        assertEquals(request5.name, response5?.name)
        assertEquals(request5.attributes, response5?.attributes)
        assertEquals(requestJson5.getString("city"), response5?.geogrophy?.city)
        assertEquals(requestJson5.getString("state"), response5?.geogrophy?.state)
        assertEquals(requestJson5.getString("country"), response5?.geogrophy?.country)
        assertEquals(requestJson5.getFloat("latitude"), response5?.geoDetails?.geolocation?.coordinate?.latitude)
        assertEquals(requestJson5.getFloat("longitude"), response5?.geoDetails?.geolocation?.coordinate?.longitude)
        assertEquals(request5.lineItem[0].categories, response5!!.lineItem[0].categories)
        assertEquals(request5.lineItem[0].currency, response5.lineItem[0].currency)
        assertEquals(request5.lineItem[0].price, response5.lineItem[0].price)
        assertEquals(request5.lineItem[0].product, response5.lineItem[0].product)
        assertEquals(request5.lineItem[0].quantity, response5.lineItem[0].quantity)
        assertEquals(request5.lineItem[0].properties, response5.lineItem[0].properties)
        assertEquals(userId, response5.userId)
        assertEquals(sessionId, response5.sessionId)
        assertEquals(deviceId, response5.deviceId)

        val requestEventUser = objectMapper.readValue(userProfileJson, EventUserWeb::class.java)
        val responseEventUser = responseBuilderUserProfile(requestEventUser.firstName)

        assertEquals(requestEventUser.firstName, responseEventUser?.standardInfo?.firstname)
        assertEquals(requestEventUser.lastName, responseEventUser?.standardInfo?.lastname)
        assertEquals(requestEventUser.email, responseEventUser?.identity?.email)
        assertNotEquals(requestEventUser.fbId,responseEventUser?.identity?.fbId)

        val requestEventUser2 = objectMapper.readValue(userProfileUpdatedJson, EventUserWeb::class.java)

        assertEquals(requestEventUser2.mobile, responseEventUser?.identity?.mobile)
        assertEquals(requestEventUser2.fbId, responseEventUser?.identity?.fbId)
        assertEquals(requestEventUser2.googleId, responseEventUser?.identity?.googleId)
        assertEquals(requestEventUser2.gender, responseEventUser?.standardInfo?.gender)

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

    private fun requestSpecificationWithAuth(body: String, token: String): RequestSpecification {
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
            val collection = db.getCollection("28_event")
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
            val collection = db.getCollection("28_eventUser")
            val searchQuery = BasicDBObject()
            searchQuery["standardInfo.firstname"] = firstName
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
















