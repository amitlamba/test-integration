import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.apache.http.HttpStatus
import org.apache.http.protocol.HTTP
import org.junit.Before
import org.junit.Test

class Demo {
    private var token = ""
    private var notLoggedInEventJson = """"""
    private var loggedInEventJson=""""""
    private var userComesFirstTimeEventJson=""""""
    @Before
    fun runBeforeClass() {

        val loginDetails = "{\"username\":\"kamalpreet.singh@userndot.com\",\"password\":\"Kamal123!\"}"
        val request = RestAssured.given()
        request.body(loginDetails)
        request.header(HTTP.CONTENT_TYPE, ContentType.JSON)
        val response = request.post("http://192.168.0.109:8080/auth/auth")
        token = response.jsonPath().get<String>("data.value.token")

        val initRequest=RestAssured.given()
        initRequest.body("")
        initRequest.contentType(ContentType.JSON)
        initRequest.header("Authorization", token)
        val responseInit = initRequest.post("http://localhost:5454/event/initialize")
        val deviceId=responseInit.jsonPath().get<String>("data.value.deviceId")
        val sessionId=responseInit.jsonPath().get<String>("data.value.sessionId")

        notLoggedInEventJson = """{
          "name":"charged112",
          "attributes":{"price":"300"},
            "identity" : {
                "deviceId":"$deviceId",
                "sessionId":"$sessionId"
            },
          "lineItem" : [
            {
              "price":200,
              "currency":"USD",
              "product":"CORE JAVA Volume 1",
              "categories":["Book", "Programming"],
              "quantity":2
            }
          ]
        }"""

        val pushProfileRequest=RestAssured.given()
        pushProfileRequest.body("")
        pushProfileRequest.contentType(ContentType.JSON)
        pushProfileRequest.header("Authorization", token)
        val responsePushProfile = pushProfileRequest.post("http://localhost:5454/push/profile")
        val deviceId2=responsePushProfile.jsonPath().get<String>("data.value.deviceId")
        val sessionId2=responsePushProfile.jsonPath().get<String>("data.value.sessionId")
        val userId2=responsePushProfile.jsonPath().get<String>("data.value.sessionId")


        loggedInEventJson = """{
          "name":"charged112",
          "attributes":{"price":"300"},
            "identity" : {
                "deviceId":"$deviceId2",
                "sessionId":"$sessionId2",
                "userId":"$userId2"
            },
          "lineItem" : [
            {
              "price":200,
              "currency":"USD",
              "product":"CORE JAVA Volume 1",
              "categories":["Book", "Programming"],
              "quantity":2
            }
          ]
        }"""

        userComesFirstTimeEventJson = """{
          "name":"charged112",
          "attributes":{"price":"300"},
          "lineItem" : [
            {
              "price":200,
              "currency":"USD",
              "product":"CORE JAVA Volume 1",
              "categories":["Book", "Programming"],
              "quantity":2
            }
          ]
        }"""
    }

    @Test
    fun validPushEventWithAuth() {
        //to test the correctness of push/event for the user with authorization credentials but not logged in
        //testing the output of push/event by creating a request with authorization header but without app user id

        val requestEvent = RestAssured.given()
        requestEvent.body(notLoggedInEventJson)
        requestEvent.contentType(ContentType.JSON)
        requestEvent.header("Authorization", token)
        val responseEvent = requestEvent.post("http://localhost:5454/push/event")
        responseEvent.then().statusCode(HttpStatus.SC_OK)

    }

    @Test
    fun validPushEventWithoutAuth() {
        //to test authorization of push/event for the user without authorization credentials and not logged in
        //testing the output of push/event by creating a request without authorization header and without app user id

        val requestEvent = RestAssured.given()
        requestEvent.body(notLoggedInEventJson)
        requestEvent.contentType(ContentType.JSON)
        val responseEvent = requestEvent.post("http://localhost:5454/push/event")
        responseEvent.then().statusCode(HttpStatus.SC_UNAUTHORIZED)
    }

    @Test
    fun userComesFirstTimePushEventWithAuth() {
        //to test the correctness of push/event for the user visiting first time with authorization credentials
        //testing the output of push/event by creating a request with authorization header but without identity

        val requestEvent = RestAssured.given()
        requestEvent.body(userComesFirstTimeEventJson)
        requestEvent.contentType(ContentType.JSON)
        requestEvent.header("Authorization", token)
        val responseEvent = requestEvent.post("http://localhost:5454/push/event")
        responseEvent.then().statusCode(HttpStatus.SC_OK)
    }
}


