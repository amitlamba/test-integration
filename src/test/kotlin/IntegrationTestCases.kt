import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.mongodb.BasicDBObject
import com.mongodb.MongoClient
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.apache.http.protocol.HTTP
import org.bson.Document
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class IntegrationTestCases {

    //Variable to hold Authorization token
    private var token = ""
    //Variables to hold Device Id, Session Id of the first time user
    private var deviceId = ""
    private var sessionId = ""

    private val userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36"
    private var objectMapper:ObjectMapper? = null

    private fun objectMapper(): ObjectMapper {
        val mapper = ObjectMapper()
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, true)
        mapper.registerModule(JavaTimeModule())
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        return mapper
    }

    @Before
    fun runBeforeClass() {
        objectMapper = objectMapper()
        //Generating Token
        val loginDetails = """{"username":"kamalpreet.singh@userndot.com","password":"Kamal123!"}"""
        val request = RestAssured.given()
        request.body(loginDetails)
        request.header(HTTP.CONTENT_TYPE, ContentType.JSON)
        val response = request.post("http://192.168.0.109:8080/auth/auth")
        token = response.jsonPath().get<String>("data.value.token")

        //Retrieving DeviceId, SessionId, UserId for the first time user
        val eventJson = """{}"""
        val requestEvent = RestAssured.given()
        requestEvent.body(eventJson)
        requestEvent.contentType(ContentType.JSON)
        requestEvent.header("Authorization", token)
        val responseEvent = requestEvent.post("http://localhost:5454/event/initialize")
        deviceId = responseEvent.jsonPath().get<String>("data.value.deviceId")
        sessionId = responseEvent.jsonPath().get<String>("data.value.sessionId")

    }

    @Test
    fun eventPushWithAuth() {

        //Testing the correctness of event push and push profile with authorization token and data saved in the database
        //Creating user event anonymously
        val eventJson = """{
            "name":"ViewedSportsCar",
            "attributes":{
                "company":"Lamborghini",
                "model":"Aventador",
                "price":"5000000",
                "colour":"Black"
            },
            "city":"New York City",
            "state":"New York",
            "country":"United States",
            "identity":{
                "deviceId":"$deviceId",
                "sessionId":"$sessionId"
                }
        }"""
        val requestEvent = RestAssured.given()
        requestEvent.body(eventJson)
        requestEvent.contentType(ContentType.JSON)
        requestEvent.header("Authorization", token)
        requestEvent.header("User-Agent", userAgent)
        val responseEvent = requestEvent.post("http://localhost:5454/push/event")
        assertEquals(200,responseEvent.statusCode)

        //Creating another user event anonymously
        val eventJson2 = """{
          "name":"chargedSportsCar",
            "attributes":{
                "Company":"Ferrari",
                "Model":"GTC4 Lusso",
                "price":200000000,
                "colour":"Matte Black"
                },
            "identity" : {
                "deviceId":"$deviceId",
                "sessionId":"$sessionId"
                },
            "lineItem" : [{
              "price":200000000,
              "currency":"INR",
              "product":"Sports Car",
              "categories":["Racing Car", "Convertable"],
              "quantity":1,
              "properties":{
                "maximumPower":"507KW",
                "maximumSpeed":"0-100 in 3.4 sec",
                "maximumTorque":"697NM"
              }
              }
            ],
            "city":"New York City",
            "state":"New York",
            "country":"The United States Of America",
          "latitude":"56.23",
          "longitude":"156.89"
        }"""
        val requestEvent2 = RestAssured.given()
        requestEvent2.body(eventJson2)
        requestEvent2.contentType(ContentType.JSON)
        requestEvent2.header("Authorization", token)
        requestEvent2.header("User-Agent", userAgent)
        val responseEvent2=requestEvent2.post("http://localhost:5454/push/event")
        assertEquals(200,responseEvent2.statusCode)

        //Creating user profile
        val userProfileJson = """{
            "firstName":"Kamalpreet",
            "lastName":"Singh",
            "identity" : {
                "deviceId": "$deviceId",
	            "sessionId": "$sessionId"
            }
        }"""
        val requestProfile = RestAssured.given()
        requestProfile.body(userProfileJson)
        requestProfile.contentType(ContentType.JSON)
        requestProfile.header("Authorization", token)
        requestProfile.header("User-Agent", userAgent)
        val responseProfile = requestProfile.post("http://localhost:5454/push/profile")
        assertEquals(200,responseProfile.statusCode)
        val userId = responseProfile.jsonPath().get<String>("data.value.userId")

        //Creating event after creating user profile
        val eventJson3 = """{
            "name":"chargedItem",
            "attributes":{
                "product":"Laptop",
                "price":"100000",
                "colour":"Black",
                "categories":"Gaming"
            },
            "city":"Gurgaon",
            "state":"Haryana",
            "country":"India",
            "identity":{
                "deviceId":"$deviceId",
                "sessionId":"$sessionId",
                "userId":"$userId"
                }
        }"""
        val requestEvent3 = RestAssured.given()
        requestEvent3.body(eventJson3)
        requestEvent3.contentType(ContentType.JSON)
        requestEvent3.header("Authorization", token)
        requestEvent3.header("User-Agent", userAgent)
        val responseEvent3=requestEvent3.post("http://localhost:5454/push/event")
        assertEquals(200,responseEvent3.statusCode)

        //Updating user profile
        val userProfileUpdatedJson = """{
            "mobile":"7015499994",
            "countryCode":"+91",
            "identity" : {
                "deviceId": "$deviceId",
	            "sessionId": "$sessionId"
            }
        }"""
        val requestProfileUpdated = RestAssured.given()
        requestProfileUpdated.body(userProfileUpdatedJson)
        requestProfileUpdated.contentType(ContentType.JSON)
        requestProfileUpdated.header("Authorization", token)
        requestProfileUpdated.header("User-Agent", userAgent)
        val responseProfileUpdated=requestProfileUpdated.post("http://localhost:5454/push/profile")
        assertEquals(200,responseProfileUpdated.statusCode)

        //Updating user profile
        val userProfileUpdatedJson2 = """{
            "country":"India",
            "identity" : {
                "deviceId": "$deviceId",
	            "sessionId": "$sessionId"
            }
        }"""
        val requestProfileUpdated2 = RestAssured.given()
        requestProfileUpdated2.body(userProfileUpdatedJson2)
        requestProfileUpdated2.contentType(ContentType.JSON)
        requestProfileUpdated2.header("Authorization", token)
        requestProfileUpdated2.header("User-Agent", userAgent)
        val responseProfileUpdated2=requestProfileUpdated2.post("http://localhost:5454/push/profile")
        assertEquals(200,responseProfileUpdated2.statusCode)

        //Creating event after updating user profile
        val eventJson4 = """{
            "name":"AddedToWishList",
            "attributes":{
                "product":"Playstation",
                "price":"34999",
                "colour":"Black",
                "categories":"Gaming"
            },
            "city":"Gurgaon",
            "state":"Haryana",
            "country":"India",
            "identity":{
                "deviceId":"$deviceId",
                "sessionId":"$sessionId",
                "userId":"$userId"
                }
        }"""
        val requestEvent4 = RestAssured.given()
        requestEvent4.body(eventJson4)
        requestEvent4.contentType(ContentType.JSON)
        requestEvent4.header("Authorization", token)
        requestEvent4.header("User-Agent", userAgent)
        val responseEvent4=requestEvent4.post("http://localhost:5454/push/event")
        assertEquals(200,responseEvent4.statusCode)

        //Checking event data in MongoDB Database

        val requestJson=JSONObject(eventJson).toString()
        val request=objectMapper?.readValue(requestJson, Event::class.java)

        val mongoClient = MongoClient("192.168.0.109", 27017)
        val db = mongoClient.getDatabase("eventdb")
        val collection = db.getCollection("13_event")
        val searchQuery = BasicDBObject()
        searchQuery["name"] = request?.name
        val cursor = collection.find(searchQuery)
        val iterableCursor=cursor.iterator()

        var responseJson=""
        while(iterableCursor.hasNext()){
            responseJson=JSONObject(iterableCursor.next()).toString()
        }
        val response = objectMapper?.readValue(responseJson, Event::class.java)

        assertEquals(request?.name,response?.name)
        assertEquals(request?.geogrophy,response?.geogrophy)
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





}


