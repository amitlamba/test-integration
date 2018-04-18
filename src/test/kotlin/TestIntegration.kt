import com.mongodb.BasicDBObject
import com.mongodb.MongoClient
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.apache.http.HttpStatus
import org.apache.http.protocol.HTTP
import org.junit.Before
import org.junit.Test

class TestIntegration {

    private var token = ""
    private var deviceId=""
    private var sessionId=""

    private val userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36"

    @Before
    fun runBeforeClass() {

        val loginDetails = """{"username":"kamalpreet.singh@userndot.com","password":"Kamal123!"}"""
        val request = RestAssured.given()
        request.body(loginDetails)
        request.header(HTTP.CONTENT_TYPE, ContentType.JSON)
        val response = request.post("http://192.168.0.109:8080/auth/auth")
        token = response.jsonPath().get<String>("data.value.token")

        val eventJson = """{
        }"""

        val requestEvent = RestAssured.given()
        requestEvent.body(eventJson)
        requestEvent.contentType(ContentType.JSON)
        requestEvent.header("Authorization", token)
        val responseEvent = requestEvent.post("http://192.168.0.109:8080/event/event/initialize")
        deviceId=responseEvent.jsonPath().get<String>("data.value.deviceId")
        sessionId=responseEvent.jsonPath().get<String>("data.value.sessionId")
    }

    @Test
    fun validPushEventWithAuth() {

        //to test the correctness of push/event for the user with authorization credentials but not logged in
        //testing the output of push/event by creating a request with authorization header but without app user id
        val eventJson = """{
          "name":"charged007",
          "attributes":{"price":"3000"},
            "identity" : {
                "deviceId":"$deviceId",
                "sessionId":"$sessionId"
            },
          "lineItem" : [
            {
              "price":2000,
              "currency":"USD",
              "product":"Spring In Action",
              "categories":["Book", "Programming"],
              "quantity":2
            }
          ],
          "city":"Gurgaon",
          "state":"Haryana",
          "country":"India"
        }"""
        val requestEvent = RestAssured.given()
        requestEvent.body(eventJson)
        requestEvent.contentType(ContentType.JSON)
        requestEvent.header("Authorization", token)
        requestEvent.header("User-Agent", userAgent)
        var responseEvent = requestEvent.post("http://192.168.0.109:8080/event/push/event")
        responseEvent.body.print()
        responseEvent = requestEvent.post("http://192.168.0.109:8080/event/push/event")
        responseEvent.body.print()

        val mongoClient=MongoClient("192.168.0.109",27017)
        val db=mongoClient.getDatabase("eventdb")
        val collection=db.getCollection("13_event")
        val searchQuery=BasicDBObject()
        searchQuery["name"] = "charged007"
        val cursor= collection.find(searchQuery)
        for (doc in cursor) {
            println(doc)
        }

    }
}


