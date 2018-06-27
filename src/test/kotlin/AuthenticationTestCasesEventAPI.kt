import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.und.model.web.EventUserWeb
import com.und.model.web.EventWeb
import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.specification.RequestSpecification
import org.apache.commons.lang3.RandomStringUtils
import org.apache.http.HttpStatus
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.File
import java.io.StringReader
import java.util.*

class AuthenticationTestCasesEventAPI {

    private var token = ""                      //Variable to hold Authorization token
    private var eventToken = ""                 //Variable to hold Event Authorization token
    private var deviceId = ""                   //Variable to hold Device Id of the first time user
    private var sessionId = ""                  //Variable to hold Session Id of the first time user

    private val userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36"
    private lateinit var objectMapper: ObjectMapper
    private val profile = "dev"

    private lateinit var authUrl: String
    private lateinit var authEventUrl: String
    private lateinit var eventUrl: String
    private lateinit var pushProfileUrl: String
    private lateinit var eventInitializeUrl: String

    @Before
    fun runBeforeClass(){

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
    fun testAuthTokenGeneratorWithCredentials(){

        val loginDetails = """{"username":"kamalpreetsingh025@gmail.com","password":"Kamalpreet123!"}"""
        val requestEvent = requestSpecificationWithOutAuth(loginDetails)
        val responseEvent = requestEvent.post(authUrl)
        assertEquals(HttpStatus.SC_OK, responseEvent.statusCode)

    }

    @Test
    fun testAuthTokenGeneratorWithoutCredentials(){

        val loginDetails = "{}"
        val requestEvent = requestSpecificationWithOutAuth(loginDetails)
        val responseEvent = requestEvent.post(authUrl)
        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, responseEvent.statusCode)

    }

    @Test
    fun testAuthTokenGeneratorWithIncorrectCredentials(){

        val loginDetails = """{"username":"random@gmail.com","password":"incorrect"}"""
        val requestEvent = requestSpecificationWithOutAuth(loginDetails)
        val responseEvent = requestEvent.post(authUrl)
        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, responseEvent.statusCode)

    }

    @Test
    fun testEventAuthTokenGeneratorWithCredentials(){

        val eventJson = "{}"
        val requestEvent = requestSpecificationWithAuth(eventJson,token)
        val responseEvent = requestEvent.post(authEventUrl)
        assertEquals(HttpStatus.SC_OK, responseEvent.statusCode)

    }

    @Test
    fun testEventAuthTokenGeneratorWithoutCredentials(){

        val eventJson = "{}"
        val requestEvent = requestSpecificationWithOutAuth(eventJson)
        val responseEvent = requestEvent.post(authEventUrl)
        assertEquals(HttpStatus.SC_FORBIDDEN, responseEvent.statusCode)

    }

    @Test
    fun testEventAuthTokenGeneratorWithIncorrectCredentials(){

        val eventJson = "{}"
        val incorrectToken = RandomStringUtils.random(256, true, true)
        val requestEvent = requestSpecificationWithAuth(eventJson,incorrectToken)
        val responseEvent = requestEvent.post(authEventUrl)
        assertEquals(HttpStatus.SC_FORBIDDEN, responseEvent.statusCode)

    }

    @Test
    fun testEventInitializeWithAuthToken(){

        val eventJson = "{}"
        val requestEvent = requestSpecificationWithAuth(eventJson,eventToken)
        val responseEvent = requestEvent.post(eventInitializeUrl)
        assertEquals(HttpStatus.SC_OK, responseEvent.statusCode)

    }

    @Test
    fun testEventInitializeWithoutAuthToken(){

        val eventJson = "{}"
        val requestEvent = requestSpecificationWithOutAuth(eventJson)
        val responseEvent = requestEvent.post(eventInitializeUrl)
        assertEquals(HttpStatus.SC_UNAUTHORIZED, responseEvent.statusCode)

    }

    @Test
    fun testEventInitializeWithIncorrectAuthToken(){

        val eventJson = "{}"
        val incorrectToken = RandomStringUtils.random(256, true, true)
        val requestEvent = requestSpecificationWithAuth(eventJson,incorrectToken)
        val responseEvent = requestEvent.post(eventInitializeUrl)
        assertEquals(HttpStatus.SC_UNAUTHORIZED, responseEvent.statusCode)

    }

    @Test
    fun testPushEventWithAuthToken(){

        val eventJson = buildEvent("event1.json",deviceId,sessionId)
        val requestEvent = requestSpecificationWithAuth(eventJson,eventToken)
        val responseEvent = requestEvent.post(eventUrl)
        assertEquals(HttpStatus.SC_OK, responseEvent.statusCode)

    }

    @Test
    fun testPushEventWithoutAuthToken(){

        val eventJson = buildEvent("event1.json",deviceId,sessionId)
        val requestEvent = requestSpecificationWithOutAuth(eventJson)
        val responseEvent = requestEvent.post(eventUrl)
        assertEquals(HttpStatus.SC_UNAUTHORIZED, responseEvent.statusCode)

    }

    @Test
    fun testPushEventWithIncorrectAuthToken(){

        val eventJson = buildEvent("event1.json",deviceId,sessionId)
        val incorrectToken = RandomStringUtils.random(256, true, true)
        val requestEvent = requestSpecificationWithAuth(eventJson,incorrectToken)
        val responseEvent = requestEvent.post(eventUrl)
        assertEquals(HttpStatus.SC_UNAUTHORIZED, responseEvent.statusCode)

    }

    @Test
    fun testPushProfileWithAuthToken(){

        val userProfileJson = buildUserProfile("userProfile1.json",deviceId,sessionId)
        val requestEvent = requestSpecificationWithAuth(userProfileJson,eventToken)
        val responseEvent = requestEvent.post(pushProfileUrl)
        assertEquals(HttpStatus.SC_OK, responseEvent.statusCode)

    }

    @Test
    fun testPushProfileWithoutAuthToken(){

        val userProfileJson = buildUserProfile("userProfile1.json",deviceId,sessionId)
        val requestEvent = requestSpecificationWithOutAuth(userProfileJson)
        val responseEvent = requestEvent.post(pushProfileUrl)
        assertEquals(HttpStatus.SC_UNAUTHORIZED, responseEvent.statusCode)

    }

    @Test
    fun testPushProfileWithIncorrectAuthToken(){

        val userProfileJson = buildUserProfile("userProfile1.json",deviceId,sessionId)
        val incorrectToken = RandomStringUtils.random(256, true, true)
        val requestEvent = requestSpecificationWithAuth(userProfileJson,incorrectToken)
        val responseEvent = requestEvent.post(pushProfileUrl)
        assertEquals(HttpStatus.SC_UNAUTHORIZED, responseEvent.statusCode)

    }

    private fun requestSpecificationWithAuth(body: String): RequestSpecification {
        val requestEvent = RestAssured.given()
        requestEvent.body(body)
        requestEvent.contentType(ContentType.JSON)
        requestEvent.header("Authorization", eventToken)
        requestEvent.header("User-Agent", userAgent)
        return requestEvent
    }

    private fun requestSpecificationWithAuth(body: String, token:String): RequestSpecification {
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


}