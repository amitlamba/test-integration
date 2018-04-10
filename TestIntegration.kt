import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.apache.http.HttpStatus
import org.apache.http.protocol.HTTP
import org.junit.Before
import org.junit.Test
import org.apache.commons.lang3.RandomStringUtils
class TestIntegration {
    //private val baseUrl = "http://192.168.0.109:8080"
    private var status = ""
    private var token = ""

    @Before
    fun runBeforeClass() {

        val loginDetails = "{\"username\":\"kamalpreet.singh@userndot.com\",\"password\":\"Kamal123!\"}"
        //RestAssured.baseURI = baseUrl
        val request = RestAssured.given()
        request.body(loginDetails)
        request.header(HTTP.CONTENT_TYPE, ContentType.JSON)
        val response = request.post("http://192.168.0.109:8080/auth/auth")
        status = response.jsonPath().get<String>("status")
        token = response.jsonPath().get<String>("data.value.token")
    }

    @Test
    fun validPushEventWithAuth() {
        //to test the correctness of push/event for the user with authorization credentials but not logged in
        //testing the output of push/event by creating a request with authorization header but without app user id

        val validEventJson = """{
          "name":"charged112",
          "attributes":{"price":"300"},
            "identity" : {
                "deviceId":"5c723e5e-becf-4de7-8943-7c15a1b0f45a",
                "sessionId":"e9298a5d-dca6-49c1-b333-f4eb6e7a2909"
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

        val requestEvent = RestAssured.given()
        requestEvent.body(validEventJson)
        requestEvent.contentType(ContentType.JSON)
        requestEvent.header("Authorization", token)
        val responseEvent = requestEvent.post("http://localhost:5454/push/event")
        val bodyResponse = responseEvent.body()
        bodyResponse.print()
        responseEvent.then().statusCode(HttpStatus.SC_OK)
    }

    @Test
    fun validPushEventWithoutAuth() {
        //to test authorization of push/event for the user without authorization credentials and not logged in
        //testing the output of push/event by creating a request without authorization header and without app user id

        val validEventJson = """{
          "name":"charged112",
          "attributes":{"price":"300"},
            "identity" : {
                "deviceId":"5c723e5e-becf-4de7-8943-7c15a1b0f45a",
                "sessionId":"e9298a5d-dca6-49c1-b333-f4eb6e7a2909"
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

        val requestEvent = RestAssured.given()
        requestEvent.body(validEventJson)
        requestEvent.contentType(ContentType.JSON)
        val responseEvent = requestEvent.post("http://localhost:5454/push/event")
        val bodyResponse = responseEvent.body()
        bodyResponse.print()
        responseEvent.then().statusCode(HttpStatus.SC_UNAUTHORIZED)
    }

    @Test
    fun userComesFirstTimePushEventWithAuth() {
        //to test the correctness of push/event for the user visiting first time with authorization credentials
        //testing the output of push/event by creating a request with authorization header but without identity

        val validEventJson = """{
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

        val requestEvent = RestAssured.given()
        requestEvent.body(validEventJson)
        requestEvent.contentType(ContentType.JSON)
        requestEvent.header("Authorization", token)
        val responseEvent = requestEvent.post("http://localhost:5454/push/event")
        val bodyResponse = responseEvent.body()
        bodyResponse.print()
        responseEvent.then().statusCode(HttpStatus.SC_OK)
    }

    @Test
    fun userComesFirstTimePushEventWithoutAuth() {
        //to test the authorization of push/event for the user visiting first time without authorization credentials
        //testing the output of push/event by creating a request without authorization header and without identity

        val validEventJson = """{
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

        val requestEvent = RestAssured.given()
        requestEvent.body(validEventJson)
        requestEvent.contentType(ContentType.JSON)
        val responseEvent = requestEvent.post("http://localhost:5454/push/event")
        val bodyResponse = responseEvent.body()
        bodyResponse.print()
        responseEvent.then().statusCode(HttpStatus.SC_UNAUTHORIZED)
    }

    @Test
    fun validPushEventWithIncorrectAuth() {
    //FIXME No Message Displayed Handle The Exception
        //to test the authorization of push/event for the user not logged in and providing incorrect authorization credentials
        //testing the output of push/event creating a request with random authorization and without identity

        val validEventJson = """{
          "name":"charged112",
          "attributes":{"price":"3000"},
            "identity" : {
                "deviceId":"5c723e5e-bc5f-4de7-8943-7c15a1b0f45a",
                "sessionId":"e9298a5d-dca6-49c1-b333-f4eb6e7a2909"
            },
          "lineItem" : [
            {
              "price":200,
              "currency":"USD",
              "product":"Spring In Action",
              "categories":["Book", "Programming"],
              "quantity":2
            }
          ]
        }"""

        val generatedString = RandomStringUtils.random(100, true, true)
        val requestEvent = RestAssured.given()
        requestEvent.body(validEventJson)
        requestEvent.contentType(ContentType.JSON)
        requestEvent.header("Authorization", generatedString)
        val responseEvent = requestEvent.post("http://localhost:5454/push/event")
        val bodyResponse = responseEvent.body()
        bodyResponse.print()
        responseEvent.then().statusCode(HttpStatus.SC_UNAUTHORIZED)
    }

    @Test
    fun validPushProfileWithAuth() {
        //to test the correctness of push/profile for the user providing correct authorization credentials
        //testing the output of push/profile by creating a request with authorization header

        val validEventJson = """{
          "firstName":"Kamalpreet",
          "lastName":"Singh",
            "identity" : {
              "deviceId": "5c723e5e-becf-4de7-8943-7c15a1b0f45a",
	          "sessionId": "e9298a5d-dca6-49c1-b333-f4eb6e7a2909",
	          "userId": "5a770a572ff9764dc8ff6a0d"
            },
          "gender":"M",
          "country":"India"
        }"""

        val requestEvent = RestAssured.given()
        requestEvent.body(validEventJson)
        requestEvent.contentType(ContentType.JSON)
        requestEvent.header("Authorization", token)
        val responseEvent = requestEvent.post("http://localhost:5454/push/profile")
        val bodyResponse = responseEvent.body()
        bodyResponse.print()
        responseEvent.then().statusCode(HttpStatus.SC_OK)
    }

    @Test
    fun validPushProfileWithoutAuth() {
        //to test the authorization of push/profile for the user without authorization credentials
        //testing the output of push/profile by creating a request without authorization header

        val validEventJson = """{
          "firstName":"Kamalpreet",
          "lastName":"Singh",
            "identity" : {
              "deviceId": "5c723e5e-becf-4de7-8943-7c15a1b0f45a",
	          "sessionId": "e9298a5d-dca6-49c1-b333-f4eb6e7a2909",
	          "userId": "5a770a572ff9764dc8ff6a0d"
            },
          "gender":"M",
          "country":"India"
        }"""

        val requestEvent = RestAssured.given()
        requestEvent.body(validEventJson)
        requestEvent.contentType(ContentType.JSON)
        val responseEvent = requestEvent.post("http://localhost:5454/push/profile")
        val bodyResponse = responseEvent.body()
        bodyResponse.print()
        responseEvent.then().statusCode(HttpStatus.SC_UNAUTHORIZED)
    }

    @Test
    fun editPushProfileWithAuth() {
        //Edit push profile with authorization
        val validEventJson = """{
          "firstName":"Kamalpreet",
          "lastName":"Singh",
            "identity" : {
              "deviceId": "5c723e5e-becf-4de7-8943-7c15a1b0f45a",
	          "sessionId": "e9298a5d-dca6-49c1-b333-f4eb6e7a2909",
	          "userId": "5a770a572ff9764dc8ff6a0d"
            }
        }"""

        val editedEventJson = """{
          "firstName":"Bruce",
          "lastName":"Wayne",
            "identity" : {
              "deviceId": "5c723e5e-becf-4de7-8943-7c15a1b0f45a",
	          "sessionId": "e9298a5d-dca6-49c1-b333-f4eb6e7a2909",
	          "userId": "5a770a572ff9764dc8ff6a0d"
            }
        }"""

        val requestEvent = RestAssured.given()
        requestEvent.body(validEventJson)
        requestEvent.contentType(ContentType.JSON)
        requestEvent.header("Authorization", token)
        val responseEvent = requestEvent.post("http://localhost:5454/push/profile")
        val bodyResponse = responseEvent.body()
        bodyResponse.print()
        responseEvent.then().statusCode(HttpStatus.SC_OK)
        val editedRequestEvent = RestAssured.given()
        editedRequestEvent.body(editedEventJson)
        editedRequestEvent.contentType(ContentType.JSON)
        editedRequestEvent.header("Authorization", token)
        val editedResponseEvent = editedRequestEvent.post("http://localhost:5454/push/profile")
        val editedBodyResponse = editedResponseEvent.body()
        editedBodyResponse.print()
        editedResponseEvent.then().statusCode(HttpStatus.SC_OK)
    }

    @Test
    fun editPushProfileWithoutAuth() {
        //Edit push profile without authorization
        val validEventJson = """{
          "firstName":"Kamalpreet",
          "lastName":"Singh",
            "identity" : {
              "deviceId": "5c723e5e-becf-4de7-8943-7c15a1b0f45a",
	          "sessionId": "e9298a5d-dca6-49c1-b333-f4eb6e7a2909",
	          "userId": "5a770a572ff9764dc8ff6a0d"
            }
        }"""

        val editedEventJson = """{
          "firstName":"Bruce",
          "lastName":"Wayne",
            "identity" : {
              "deviceId": "5c723e5e-becf-4de7-8943-7c15a1b0f45a",
	          "sessionId": "e9298a5d-dca6-49c1-b333-f4eb6e7a2909",
	          "userId": "5a770a572ff9764dc8ff6a0d"
            }
        }"""

        val requestEvent = RestAssured.given()
        requestEvent.body(validEventJson)
        requestEvent.contentType(ContentType.JSON)
        requestEvent.header("Authorization", token)
        val responseEvent = requestEvent.post("http://localhost:5454/push/profile")
        val bodyResponse = responseEvent.body()
        bodyResponse.print()
        responseEvent.then().statusCode(HttpStatus.SC_OK)
        val editedRequestEvent = RestAssured.given()
        editedRequestEvent.body(editedEventJson)
        editedRequestEvent.contentType(ContentType.JSON)
        val editedResponseEvent = editedRequestEvent.post("http://localhost:5454/push/profile")
        val editedBodyResponse = editedResponseEvent.body()
        editedBodyResponse.print()
        editedResponseEvent.then().statusCode(HttpStatus.SC_UNAUTHORIZED)
    }

    @Test
    fun invalidPushProfileWithoutAuth() {
    //Invalid push profile without authorization
        val validEventJson = """{
          "lastName":"Singh",
            "identity" : {
              "deviceId": "5c723e5e-becf-4de7-8943-7c15a1b0f45a",
	          "sessionId": "e9298a5d-dca6-49c1-b333-f4eb6e7a2909",
	          "userId": "5a770a572ff9764dc8ff6a0d"
            },
          "gender":"M",
          "country":"India"
        }"""

        val requestEvent = RestAssured.given()
        requestEvent.body(validEventJson)
        requestEvent.contentType(ContentType.JSON)
        val responseEvent = requestEvent.post("http://localhost:5454/push/profile")
        val bodyResponse = responseEvent.body()
        bodyResponse.print()
        responseEvent.then().statusCode(HttpStatus.SC_UNAUTHORIZED)
    }

    @Test
    fun validPushProfileWithIncorrectAuth() {
    //Valid push profile with incorrect authorization
        val validEventJson = """{
          "firstName":"Kamalpreet",
          "lastName":"Singh",
            "identity" : {
              "deviceId": "5c723e5e-becf-4de7-8943-7c15a1b0f45a",
	          "sessionId": "e9298a5d-dca6-49c1-b333-f4eb6e7a2909",
	          "userId": "5a770a572ff9764dc8ff6a0d"
            },
          "gender":"M",
          "country":"India"
        }"""

        val generatedString = RandomStringUtils.random(100, true, true)
        val requestEvent = RestAssured.given()
        requestEvent.body(validEventJson)
        requestEvent.contentType(ContentType.JSON)
        requestEvent.header("Authorization", generatedString)
        val responseEvent = requestEvent.post("http://localhost:5454/push/profile")
        val bodyResponse = responseEvent.body()
        bodyResponse.print()
        responseEvent.then().statusCode(HttpStatus.SC_UNAUTHORIZED)
    }

    @Test
    fun invalidPushProfileWithIncorrectAuth() {
        //Invalid push profile with incorrect authorization
        val validEventJson = """{
          "lastName":"Singh",
            "identity" : {
              "deviceId": "5c723e5e-becf-4de7-8943-7c15a1b0f45a",
	          "sessionId": "e9298a5d-dca6-49c1-b333-f4eb6e7a2909",
	          "userId": "5a770a572ff9764dc8ff6a0d"
            },
          "gender":"M",
          "country":"India"
        }"""

        val generatedString = RandomStringUtils.random(100, true, true)
        val requestEvent = RestAssured.given()
        requestEvent.body(validEventJson)
        requestEvent.contentType(ContentType.JSON)
        requestEvent.header("Authorization", generatedString)
        val responseEvent = requestEvent.post("http://localhost:5454/push/profile")
        val bodyResponse = responseEvent.body()
        bodyResponse.print()
        responseEvent.then().statusCode(HttpStatus.SC_UNAUTHORIZED)
    }

    @Test
    fun firstTimeUserValidPushProfileWithAuth() {
        //Valid push profile with authorization for first time user
        val validEventJson = """{
          "firstName":"Kamalpreet",
          "lastName":"Singh",
            "identity" : {
            },
          "gender":"M",
          "country":"India"
        }"""

        val requestEvent = RestAssured.given()
        requestEvent.body(validEventJson)
        requestEvent.contentType(ContentType.JSON)
        requestEvent.header("Authorization", token)
        val responseEvent = requestEvent.post("http://localhost:5454/push/profile")
        val bodyResponse = responseEvent.body()
        bodyResponse.print()
        responseEvent.then().statusCode(HttpStatus.SC_OK)
    }

    @Test
    fun firstTimeUserValidPushProfileWithoutAuth() {
        //Valid push profile with authorization for first time user
        val validEventJson = """{
          "firstName":"Kamalpreet",
          "lastName":"Singh",
            "identity" : {
            },
          "gender":"M",
          "country":"India"
        }"""

        val requestEvent = RestAssured.given()
        requestEvent.body(validEventJson)
        requestEvent.contentType(ContentType.JSON)
        val responseEvent = requestEvent.post("http://localhost:5454/push/profile")
        val bodyResponse = responseEvent.body()
        bodyResponse.print()
        responseEvent.then().statusCode(HttpStatus.SC_UNAUTHORIZED)
    }

    @Test
    fun firstTimeUserInvalidPushProfileWithoutAuth() {
        //Invalid push profile without authorization for first time user
        val validEventJson = """{
          "lastName":"Singh",
            "identity" : {
            },
          "gender":"M",
          "country":"India"
        }"""

        val requestEvent = RestAssured.given()
        requestEvent.body(validEventJson)
        requestEvent.contentType(ContentType.JSON)
        val responseEvent = requestEvent.post("http://localhost:5454/push/profile")
        val bodyResponse = responseEvent.body()
        bodyResponse.print()
        responseEvent.then().statusCode(HttpStatus.SC_UNAUTHORIZED)
    }

    @Test
    fun emptyNamePushEvent() {
    //Event name not provided
        val validEventJson = """{
          "name":"",
          "identity" : {
                "deviceId":"5c723e5e-becf-4de7-8943-7c15a1b0f45a",
                "sessionId":"e9298a5d-dca6-49c1-b333-f4eb6e7a2909"
            }
        }"""

        val requestEvent = RestAssured.given()
        requestEvent.body(validEventJson)
        requestEvent.contentType(ContentType.JSON)
        requestEvent.header("Authorization", token)
        val responseEvent = requestEvent.post("http://localhost:5454/push/event")
        val bodyResponse = responseEvent.body()
        bodyResponse.print()
        responseEvent.then().statusCode(HttpStatus.SC_NOT_ACCEPTABLE)
    }

    @Test
    fun invalidNameSizePushEvent() {
    //Event name contains invalid no of characters
        val validEventJson = """{
          "name":"A",
          "identity" : {
                "deviceId":"5c723e5e-becf-4de7-8943-7c15a1b0f45a",
                "sessionId":"e9298a5d-dca6-49c1-b333-f4eb6e7a2909"
            }
        }"""

        val requestEvent = RestAssured.given()
        requestEvent.body(validEventJson)
        requestEvent.contentType(ContentType.JSON)
        requestEvent.header("Authorization", token)
        val responseEvent = requestEvent.post("http://localhost:5454/push/event")
        val bodyResponse = responseEvent.body()
        bodyResponse.print()
        responseEvent.then().statusCode(HttpStatus.SC_NOT_ACCEPTABLE)
    }

    @Test
    fun invalidClientIdPushEvent() {
    //Invalid Client Id
        val validEventJson = """{
          "name":"viewed product",
          "clientId":"-56",
          "identity" : {
                "deviceId":"5c723e5e-becf-4de7-8943-7c15a1b0f45a",
                "sessionId":"e9298a5d-dca6-49c1-b333-f4eb6e7a2909"
            }
        }"""

        val requestEvent = RestAssured.given()
        requestEvent.body(validEventJson)
        requestEvent.contentType(ContentType.JSON)
        requestEvent.header("Authorization", token)
        val responseEvent = requestEvent.post("http://localhost:5454/push/event")
        val bodyResponse = responseEvent.body()
        bodyResponse.print()
        responseEvent.then().statusCode(HttpStatus.SC_NOT_ACCEPTABLE)
    }

    @Test
    fun invalidClientIdDataPushEvent() {
        //FIXME Message Not Readable if Data is other than integer type
        //Invalid Client Id
        val validEventJson = """{
          "name":"viewed product",
          "clientId":"-56fnnwi",
          "identity" : {
                "deviceId":"5c723e5e-becf-4de7-8943-7c15a1b0f45a",
                "sessionId":"e9298a5d-dca6-49c1-b333-f4eb6e7a2909"
            }
        }"""

        val requestEvent = RestAssured.given()
        requestEvent.body(validEventJson)
        requestEvent.contentType(ContentType.JSON)
        requestEvent.header("Authorization", token)
        val responseEvent = requestEvent.post("http://localhost:5454/push/event")
        val bodyResponse = responseEvent.body()
        bodyResponse.print()
        responseEvent.then().statusCode(HttpStatus.SC_NOT_ACCEPTABLE)
    }

    @Test
    fun validClientIdEventPush() {
    //Valid ClientId
        val validEventJson = """{
          "name":"viewed product",
          "clientId":"256",
          "identity" : {
                "deviceId":"5c723e5e-becf-4de7-8943-7c15a1b0f45a",
                "sessionId":"e9298a5d-dca6-49c1-b333-f4eb6e7a2909"
            }
        }"""

        val requestEvent = RestAssured.given()
        requestEvent.body(validEventJson)
        requestEvent.contentType(ContentType.JSON)
        requestEvent.header("Authorization", token)
        val responseEvent = requestEvent.post("http://localhost:5454/push/event")
        val bodyResponse = responseEvent.body()
        bodyResponse.print()
        responseEvent.then().statusCode(HttpStatus.SC_OK)
    }


    @Test
    fun invalidIPAddressPushEvent() {
    //Invalid Ip Address
        val validEventJson = """{
          "name":"viewed product",
          "clientId":"256",
          "identity" : {
                "deviceId":"5c723e5e-becf-4de7-8943-7c15a1b0f45a",
                "sessionId":"e9298a5d-dca6-49c1-b333-f4eb6e7a2909"
            },
          "ipAddress":"322.256.890"
        }"""

        val requestEvent = RestAssured.given()
        requestEvent.body(validEventJson)
        requestEvent.contentType(ContentType.JSON)
        requestEvent.header("Authorization", token)
        val responseEvent = requestEvent.post("http://localhost:5454/push/event")
        val bodyResponse = responseEvent.body()
        bodyResponse.print()
        responseEvent.then().statusCode(HttpStatus.SC_NOT_ACCEPTABLE)
    }


    @Test
    fun validIPAddressPushEvent() {
    //Valid Ip address
        val validEventJson = """{
          "name":"viewed product",
          "clientId":"256",
          "identity" : {
                "deviceId":"5c723e5e-becf-4de7-8943-7c15a1b0f45a",
                "sessionId":"e9298a5d-dca6-49c1-b333-f4eb6e7a2909"
            },
          "ipAddress":"192.168.0.109"
        }"""

        val requestEvent = RestAssured.given()
        requestEvent.body(validEventJson)
        requestEvent.contentType(ContentType.JSON)
        requestEvent.header("Authorization", token)
        val responseEvent = requestEvent.post("http://localhost:5454/push/event")
        val bodyResponse = responseEvent.body()
        bodyResponse.print()
        responseEvent.then().statusCode(HttpStatus.SC_OK)
    }

    @Test
    fun invalidCityNamePushEvent() {
    //Invalid city name
        val validEventJson = """{
          "name":"viewed product",
          "clientId":"256",
          "identity" : {
                "deviceId":"5c723e5e-becf-4de7-8943-7c15a1b0f45a",
                "sessionId":"e9298a5d-dca6-49c1-b333-f4eb6e7a2909"
            },
          "ipAddress":"192.168.0.109",
          "city":"Noah890$"
        }"""

        val requestEvent = RestAssured.given()
        requestEvent.body(validEventJson)
        requestEvent.contentType(ContentType.JSON)
        requestEvent.header("Authorization", token)
        val responseEvent = requestEvent.post("http://localhost:5454/push/event")
        val bodyResponse = responseEvent.body()
        bodyResponse.print()
        responseEvent.then().statusCode(HttpStatus.SC_NOT_ACCEPTABLE)
    }

    @Test
    fun invalidCityNameSizeEventPush() {
    //Invalid city name characters
        val validEventJson = """{
          "name":"viewed product",
          "clientId":"256",
          "identity" : {
                "deviceId":"5c723e5e-becf-4de7-8943-7c15a1b0f45a",
                "sessionId":"e9298a5d-dca6-49c1-b333-f4eb6e7a2909"
            },
          "ipAddress":"192.168.0.109",
          "city":"k"
        }"""

        val requestEvent = RestAssured.given()
        requestEvent.body(validEventJson)
        requestEvent.contentType(ContentType.JSON)
        requestEvent.header("Authorization", token)
        val responseEvent = requestEvent.post("http://localhost:5454/push/event")
        val bodyResponse = responseEvent.body()
        bodyResponse.print()
        responseEvent.then().statusCode(HttpStatus.SC_NOT_ACCEPTABLE)
    }

    @Test
    fun validCityNamePushEvent() {
    //Valid city name
        val validEventJson = """{
          "name":"viewed product",
          "clientId":"256",
          "identity" : {
                "deviceId":"5c723e5e-becf-4de7-8943-7c15a1b0f45a",
                "sessionId":"e9298a5d-dca6-49c1-b333-f4eb6e7a2909"
            },
          "ipAddress":"192.168.0.109",
          "city":"Gotham"
        }"""

        val requestEvent = RestAssured.given()
        requestEvent.body(validEventJson)
        requestEvent.contentType(ContentType.JSON)
        requestEvent.header("Authorization", token)
        val responseEvent = requestEvent.post("http://localhost:5454/push/event")
        val bodyResponse = responseEvent.body()
        bodyResponse.print()
        responseEvent.then().statusCode(HttpStatus.SC_OK)
    }

    @Test
    fun invalidStateNamePushEvent() {
    //Invalid State name
        val validEventJson = """{
          "name":"viewed product",
          "clientId":"256",
          "identity" : {
                "deviceId":"5c723e5e-becf-4de7-8943-7c15a1b0f45a",
                "sessionId":"e9298a5d-dca6-49c1-b333-f4eb6e7a2909"
            },
          "ipAddress":"192.168.0.109",
          "city":"Gotham",
          "state":"new78#@"
        }"""

        val requestEvent = RestAssured.given()
        requestEvent.body(validEventJson)
        requestEvent.contentType(ContentType.JSON)
        requestEvent.header("Authorization", token)
        val responseEvent = requestEvent.post("http://localhost:5454/push/event")
        val bodyResponse = responseEvent.body()
        bodyResponse.print()
        responseEvent.then().statusCode(HttpStatus.SC_NOT_ACCEPTABLE)
    }

    @Test
    fun invalidStateNameSizePushEvent() {
    //Invalid state name no of characters
        val validEventJson = """{
          "name":"viewed product",
          "clientId":"256",
          "identity" : {
                "deviceId":"5c723e5e-becf-4de7-8943-7c15a1b0f45a",
                "sessionId":"e9298a5d-dca6-49c1-b333-f4eb6e7a2909"
            },
          "ipAddress":"192.168.0.109",
          "city":"Gotham",
          "state":"L"
        }"""

        val requestEvent = RestAssured.given()
        requestEvent.body(validEventJson)
        requestEvent.contentType(ContentType.JSON)
        requestEvent.header("Authorization", token)
        val responseEvent = requestEvent.post("http://localhost:5454/push/event")
        val bodyResponse = responseEvent.body()
        bodyResponse.print()
        responseEvent.then().statusCode(HttpStatus.SC_NOT_ACCEPTABLE)
    }

    @Test
    fun validStateNamePushEvent() {
    //Valid state name
        val validEventJson = """{
          "name":"viewed product",
          "clientId":"256",
          "identity" : {
                "deviceId":"5c723e5e-becf-4de7-8943-7c15a1b0f45a",
                "sessionId":"e9298a5d-dca6-49c1-b333-f4eb6e7a2909"
            },
          "ipAddress":"192.168.0.109",
          "city":"Gotham",
          "state":"New Jersey"
        }"""

        val requestEvent = RestAssured.given()
        requestEvent.body(validEventJson)
        requestEvent.contentType(ContentType.JSON)
        requestEvent.header("Authorization", token)
        val responseEvent = requestEvent.post("http://localhost:5454/push/event")
        val bodyResponse = responseEvent.body()
        bodyResponse.print()
        responseEvent.then().statusCode(HttpStatus.SC_OK)
    }

    @Test
    fun invalidCountryNamePushEvent() {
    //Invalid country name
        val validEventJson = """{
          "name":"viewed product",
          "clientId":"256",
          "identity" : {
                "deviceId":"5c723e5e-becf-4de7-8943-7c15a1b0f45a",
                "sessionId":"e9298a5d-dca6-49c1-b333-f4eb6e7a2909"
            },
          "ipAddress":"192.168.0.109",
          "city":"Gotham",
          "state":"New Jersey",
          "country":"United S7855#2"
        }"""

        val requestEvent = RestAssured.given()
        requestEvent.body(validEventJson)
        requestEvent.contentType(ContentType.JSON)
        requestEvent.header("Authorization", token)
        val responseEvent = requestEvent.post("http://localhost:5454/push/event")
        val bodyResponse = responseEvent.body()
        bodyResponse.print()
        responseEvent.then().statusCode(HttpStatus.SC_NOT_ACCEPTABLE)
    }

    @Test
    fun invalidCountryNameSizePushEvent() {
    //Invalid country name no of characters
        val validEventJson = """{
          "name":"viewed product",
          "clientId":"256",
          "identity" : {
                "deviceId":"5c723e5e-becf-4de7-8943-7c15a1b0f45a",
                "sessionId":"e9298a5d-dca6-49c1-b333-f4eb6e7a2909"
            },
          "ipAddress":"192.168.0.109",
          "city":"Gotham",
          "state":"New Jersey",
          "country":"P"
        }"""

        val requestEvent = RestAssured.given()
        requestEvent.body(validEventJson)
        requestEvent.contentType(ContentType.JSON)
        requestEvent.header("Authorization", token)
        val responseEvent = requestEvent.post("http://localhost:5454/push/event")
        val bodyResponse = responseEvent.body()
        bodyResponse.print()
        responseEvent.then().statusCode(HttpStatus.SC_NOT_ACCEPTABLE)
    }

    @Test
    fun validCountryNamePushEvent() {
    //Valid Country name
        val validEventJson = """{
          "name":"viewed product",
          "clientId":"256",
          "identity" : {
                "deviceId":"5c723e5e-becf-4de7-8943-7c15a1b0f45a",
                "sessionId":"e9298a5d-dca6-49c1-b333-f4eb6e7a2909"
            },
          "ipAddress":"192.168.0.109",
          "city":"Gotham",
          "state":"New Jersey",
          "country":"United States Of America"
        }"""

        val requestEvent = RestAssured.given()
        requestEvent.body(validEventJson)
        requestEvent.contentType(ContentType.JSON)
        requestEvent.header("Authorization", token)
        val responseEvent = requestEvent.post("http://localhost:5454/push/event")
        val bodyResponse = responseEvent.body()
        bodyResponse.print()
        responseEvent.then().statusCode(HttpStatus.SC_OK)
    }

    @Test
    fun invalidLatitudePushEvent() {
    //Invalid Latitude coordinates
        val validEventJson = """{
          "name":"viewed product",
          "clientId":"256",
          "identity" : {
                "deviceId":"5c723e5e-becf-4de7-8943-7c15a1b0f45a",
                "sessionId":"e9298a5d-dca6-49c1-b333-f4eb6e7a2909"
            },
          "ipAddress":"192.168.0.109",
          "city":"Gotham",
          "state":"New Jersey",
          "country":"United States Of America",
          "latitude":"+562.23cbdsjc"
        }"""

        val requestEvent = RestAssured.given()
        requestEvent.body(validEventJson)
        requestEvent.contentType(ContentType.JSON)
        requestEvent.header("Authorization", token)
        val responseEvent = requestEvent.post("http://localhost:5454/push/event")
        val bodyResponse = responseEvent.body()
        bodyResponse.print()
        responseEvent.then().statusCode(HttpStatus.SC_NOT_ACCEPTABLE)
    }

    @Test
    fun validLatitudePushEvent() {
    //Valid latitude coordinates
        val validEventJson = """{
          "name":"viewed product",
          "clientId":"256",
          "identity" : {
                "deviceId":"5c723e5e-becf-4de7-8943-7c15a1b0f45a",
                "sessionId":"e9298a5d-dca6-49c1-b333-f4eb6e7a2909"
            },
          "ipAddress":"192.168.0.109",
          "city":"Gotham",
          "state":"New Jersey",
          "country":"United States Of America",
          "latitude":"56.23"
        }"""

        val requestEvent = RestAssured.given()
        requestEvent.body(validEventJson)
        requestEvent.contentType(ContentType.JSON)
        requestEvent.header("Authorization", token)
        val responseEvent = requestEvent.post("http://localhost:5454/push/event")
        val bodyResponse = responseEvent.body()
        bodyResponse.print()
        responseEvent.then().statusCode(HttpStatus.SC_OK)
    }

    @Test
    fun invalidLongitudePushEvent() {
    //Invalid longitude coordinates
        val validEventJson = """{
          "name":"viewed product",
          "clientId":"256",
          "identity" : {
                "deviceId":"5c723e5e-becf-4de7-8943-7c15a1b0f45a",
                "sessionId":"e9298a5d-dca6-49c1-b333-f4eb6e7a2909"
            },
          "ipAddress":"192.168.0.109",
          "city":"Gotham",
          "state":"New Jersey",
          "country":"United States Of America",
          "longitude":"-563+3"
        }"""

        val requestEvent = RestAssured.given()
        requestEvent.body(validEventJson)
        requestEvent.contentType(ContentType.JSON)
        requestEvent.header("Authorization", token)
        val responseEvent = requestEvent.post("http://localhost:5454/push/event")
        val bodyResponse = responseEvent.body()
        bodyResponse.print()
        responseEvent.then().statusCode(HttpStatus.SC_NOT_ACCEPTABLE)
    }

    @Test
    fun validLongitudePushEvent() {
    //Valid longitude coordinates
        val validEventJson = """{
          "name":"viewed product",
          "clientId":"256",
          "identity" : {
                "deviceId":"5c723e5e-becf-4de7-8943-7c15a1b0f45a",
                "sessionId":"e9298a5d-dca6-49c1-b333-f4eb6e7a2909"
            },
          "ipAddress":"192.168.0.109",
          "city":"Gotham",
          "state":"New Jersey",
          "country":"United States Of America",
          "longitude":"156.24"
        }"""

        val requestEvent = RestAssured.given()
        requestEvent.body(validEventJson)
        requestEvent.contentType(ContentType.JSON)
        requestEvent.header("Authorization", token)
        val responseEvent = requestEvent.post("http://localhost:5454/push/event")
        val bodyResponse = responseEvent.body()
        bodyResponse.print()
        responseEvent.then().statusCode(HttpStatus.SC_OK)
    }

    @Test
    fun invalidFirstNameSizePushProfile() {
    //Invalid first name no of characters
        val validEventJson = """{
          "firstName":"K",
            "identity" : {
              "deviceId": "5c723e5e-becf-4de7-8943-7c15a1b0f45a",
	          "sessionId": "e9298a5d-dca6-49c1-b333-f4eb6e7a2909",
	          "userId": "5a770a572ff9764dc8ff6a0d"
            },
          "gender":"M",
          "country":"India"
        }"""

        val requestEvent = RestAssured.given()
        requestEvent.body(validEventJson)
        requestEvent.contentType(ContentType.JSON)
        requestEvent.header("Authorization", token)
        val responseEvent = requestEvent.post("http://localhost:5454/push/profile")
        val bodyResponse = responseEvent.body()
        bodyResponse.print()
        responseEvent.then().statusCode(HttpStatus.SC_NOT_ACCEPTABLE)
    }

    @Test
    fun invalidFirstNamePushProfile() {
    //Invalid first name
        val validEventJson = """{
          "firstName":"K@m@89",
            "identity" : {
              "deviceId": "5c723e5e-becf-4de7-8943-7c15a1b0f45a",
	          "sessionId": "e9298a5d-dca6-49c1-b333-f4eb6e7a2909",
	          "userId": "5a770a572ff9764dc8ff6a0d"
            },
          "gender":"M",
          "country":"India"
        }"""

        val requestEvent = RestAssured.given()
        requestEvent.body(validEventJson)
        requestEvent.contentType(ContentType.JSON)
        requestEvent.header("Authorization", token)
        val responseEvent = requestEvent.post("http://localhost:5454/push/profile")
        val bodyResponse = responseEvent.body()
        bodyResponse.print()
        responseEvent.then().statusCode(HttpStatus.SC_NOT_ACCEPTABLE)
    }

    @Test
    fun validFirstNamePushProfile() {
    //Valid first name
        val validEventJson = """{
          "firstName":"Kamalpreet",
            "identity" : {
              "deviceId": "5c723e5e-becf-4de7-8943-7c15a1b0f45a",
	          "sessionId": "e9298a5d-dca6-49c1-b333-f4eb6e7a2909",
	          "userId": "5a770a572ff9764dc8ff6a0d"
            },
          "gender":"M",
          "country":"India"
        }"""

        val requestEvent = RestAssured.given()
        requestEvent.body(validEventJson)
        requestEvent.contentType(ContentType.JSON)
        requestEvent.header("Authorization", token)
        val responseEvent = requestEvent.post("http://localhost:5454/push/profile")
        val bodyResponse = responseEvent.body()
        bodyResponse.print()
        responseEvent.then().statusCode(HttpStatus.SC_OK)
    }

    @Test
    fun invalidLastNameSizePushProfile() {
    //Invalid last name no of characters
        val validEventJson = """{
          "firstName":"Kamalpreet",
          "lastName":"S",
            "identity" : {
              "deviceId": "5c723e5e-becf-4de7-8943-7c15a1b0f45a",
	          "sessionId": "e9298a5d-dca6-49c1-b333-f4eb6e7a2909",
	          "userId": "5a770a572ff9764dc8ff6a0d"
            },
          "gender":"M",
          "country":"India"
        }"""

        val requestEvent = RestAssured.given()
        requestEvent.body(validEventJson)
        requestEvent.contentType(ContentType.JSON)
        requestEvent.header("Authorization", token)
        val responseEvent = requestEvent.post("http://localhost:5454/push/profile")
        val bodyResponse = responseEvent.body()
        bodyResponse.print()
        responseEvent.then().statusCode(HttpStatus.SC_NOT_ACCEPTABLE)
    }

    @Test
    fun invalidLastNamePushProfile() {
    //Invalid last name
        val validEventJson = """{
          "firstName":"Kamalpreet",
          "lastName":"S#786",
            "identity" : {
              "deviceId": "5c723e5e-becf-4de7-8943-7c15a1b0f45a",
	          "sessionId": "e9298a5d-dca6-49c1-b333-f4eb6e7a2909",
	          "userId": "5a770a572ff9764dc8ff6a0d"
            },
          "gender":"M",
          "country":"India"
        }"""

        val requestEvent = RestAssured.given()
        requestEvent.body(validEventJson)
        requestEvent.contentType(ContentType.JSON)
        requestEvent.header("Authorization", token)
        val responseEvent = requestEvent.post("http://localhost:5454/push/profile")
        val bodyResponse = responseEvent.body()
        bodyResponse.print()
        responseEvent.then().statusCode(HttpStatus.SC_NOT_ACCEPTABLE)
    }

    @Test
    fun validLastNamePushProfile() {
    //Valid last name
        val validEventJson = """{
          "firstName":"Kamalpreet",
          "lastName":"Singh",
            "identity" : {
              "deviceId": "5c723e5e-becf-4de7-8943-7c15a1b0f45a",
	          "sessionId": "e9298a5d-dca6-49c1-b333-f4eb6e7a2909",
	          "userId": "5a770a572ff9764dc8ff6a0d"
            },
          "gender":"M",
          "country":"India"
        }"""

        val requestEvent = RestAssured.given()
        requestEvent.body(validEventJson)
        requestEvent.contentType(ContentType.JSON)
        requestEvent.header("Authorization", token)
        val responseEvent = requestEvent.post("http://localhost:5454/push/profile")
        val bodyResponse = responseEvent.body()
        bodyResponse.print()
        responseEvent.then().statusCode(HttpStatus.SC_OK)
    }

    @Test
    fun invalidMobileNumberPushProfile() {
    //Invalid mobile no digits
        val validEventJson = """{
          "firstName":"Kamalpreet",
          "lastName":"Singh",
          "mobile":"bcdsjcvjls5698",
            "identity" : {
              "deviceId": "5c723e5e-becf-4de7-8943-7c15a1b0f45a",
	          "sessionId": "e9298a5d-dca6-49c1-b333-f4eb6e7a2909",
	          "userId": "5a770a572ff9764dc8ff6a0d"
            },
          "gender":"M",
          "country":"India"
        }"""

        val requestEvent = RestAssured.given()
        requestEvent.body(validEventJson)
        requestEvent.contentType(ContentType.JSON)
        requestEvent.header("Authorization", token)
        val responseEvent = requestEvent.post("http://localhost:5454/push/profile")
        val bodyResponse = responseEvent.body()
        bodyResponse.print()
        responseEvent.then().statusCode(HttpStatus.SC_NOT_ACCEPTABLE)
    }

    @Test
    fun invalidMobileNumberSizePushProfile() {
    //Invalid no. of digits of mobile no.
        val validEventJson = """{
          "firstName":"Kamalpreet",
          "lastName":"Singh",
          "mobile":"5454",
            "identity" : {
              "deviceId": "5c723e5e-becf-4de7-8943-7c15a1b0f45a",
	          "sessionId": "e9298a5d-dca6-49c1-b333-f4eb6e7a2909",
	          "userId": "5a770a572ff9764dc8ff6a0d"
            },
          "gender":"M",
          "country":"India"
        }"""

        val requestEvent = RestAssured.given()
        requestEvent.body(validEventJson)
        requestEvent.contentType(ContentType.JSON)
        requestEvent.header("Authorization", token)
        val responseEvent = requestEvent.post("http://localhost:5454/push/profile")
        val bodyResponse = responseEvent.body()
        bodyResponse.print()
        responseEvent.then().statusCode(HttpStatus.SC_NOT_ACCEPTABLE)
    }

    @Test
    fun validMobileNumberPushProfile() {
    //Valid mobile no.
        val validEventJson = """{
          "firstName":"Kamalpreet",
          "lastName":"Singh",
          "mobile":"9416923222",
            "identity" : {
              "deviceId": "5c723e5e-becf-4de7-8943-7c15a1b0f45a",
	          "sessionId": "e9298a5d-dca6-49c1-b333-f4eb6e7a2909",
	          "userId": "5a770a572ff9764dc8ff6a0d"
            },
          "gender":"M",
          "country":"India"
        }"""

        val requestEvent = RestAssured.given()
        requestEvent.body(validEventJson)
        requestEvent.contentType(ContentType.JSON)
        requestEvent.header("Authorization", token)
        val responseEvent = requestEvent.post("http://localhost:5454/push/profile")
        val bodyResponse = responseEvent.body()
        bodyResponse.print()
        responseEvent.then().statusCode(HttpStatus.SC_OK)
    }

    @Test
    fun invalidCountryNamePushProfile() {
    //Invalid country name
        val validEventJson = """{
          "firstName":"Kamalpreet",
          "lastName":"Singh",
          "mobile":"9416923222",
            "identity" : {
              "deviceId": "5c723e5e-becf-4de7-8943-7c15a1b0f45a",
	          "sessionId": "e9298a5d-dca6-49c1-b333-f4eb6e7a2909",
	          "userId": "5a770a572ff9764dc8ff6a0d"
            },
          "gender":"M",
          "country":"M45"
        }"""

        val requestEvent = RestAssured.given()
        requestEvent.body(validEventJson)
        requestEvent.contentType(ContentType.JSON)
        requestEvent.header("Authorization", token)
        val responseEvent = requestEvent.post("http://localhost:5454/push/profile")
        val bodyResponse = responseEvent.body()
        bodyResponse.print()
        responseEvent.then().statusCode(HttpStatus.SC_NOT_ACCEPTABLE)
    }


    @Test
    fun invalidCountryNameSizePushProfile() {
    //Invalid no of characters in country name
        val validEventJson = """{
          "firstName":"Kamalpreet",
          "lastName":"Singh",
          "mobile":"9416923222",
            "identity" : {
              "deviceId": "5c723e5e-becf-4de7-8943-7c15a1b0f45a",
	          "sessionId": "e9298a5d-dca6-49c1-b333-f4eb6e7a2909",
	          "userId": "5a770a572ff9764dc8ff6a0d"
            },
          "gender":"M",
          "country":"I"
        }"""

        val requestEvent = RestAssured.given()
        requestEvent.body(validEventJson)
        requestEvent.contentType(ContentType.JSON)
        requestEvent.header("Authorization", token)
        val responseEvent = requestEvent.post("http://localhost:5454/push/profile")
        val bodyResponse = responseEvent.body()
        bodyResponse.print()
        responseEvent.then().statusCode(HttpStatus.SC_NOT_ACCEPTABLE)
    }

    @Test
    fun validCountryNamePushProfile() {
    //Valid country name
        val validEventJson = """{
          "firstName":"Kamalpreet",
          "lastName":"Singh",
          "mobile":"9416923222",
            "identity" : {
              "deviceId": "5c723e5e-becf-4de7-8943-7c15a1b0f45a",
	          "sessionId": "e9298a5d-dca6-49c1-b333-f4eb6e7a2909",
	          "userId": "5a770a572ff9764dc8ff6a0d"
            },
          "gender":"M",
          "country":"India"
        }"""

        val requestEvent = RestAssured.given()
        requestEvent.body(validEventJson)
        requestEvent.contentType(ContentType.JSON)
        requestEvent.header("Authorization", token)
        val responseEvent = requestEvent.post("http://localhost:5454/push/profile")
        val bodyResponse = responseEvent.body()
        bodyResponse.print()
        responseEvent.then().statusCode(HttpStatus.SC_OK)
    }

    @Test
    fun invalidDobPushProfile() {
    //Invalid date of birth
        val validEventJson = """{
          "firstName":"Kamalpreet",
          "lastName":"Singh",
          "mobile":"9416923222",
            "identity" : {
              "deviceId": "5c723e5e-becf-4de7-8943-7c15a1b0f45a",
	          "sessionId": "e9298a5d-dca6-49c1-b333-f4eb6e7a2909",
	          "userId": "5a770a572ff9764dc8ff6a0d"
            },
          "gender":"M",
          "country":"India",
          "dob":"56234-56-12"
        }"""

        val requestEvent = RestAssured.given()
        requestEvent.body(validEventJson)
        requestEvent.contentType(ContentType.JSON)
        requestEvent.header("Authorization", token)
        val responseEvent = requestEvent.post("http://localhost:5454/push/profile")
        val bodyResponse = responseEvent.body()
        bodyResponse.print()
        responseEvent.then().statusCode(HttpStatus.SC_NOT_ACCEPTABLE)
    }

    @Test
    fun validDobPushProfile() {
    //Valid date of birth
        val validEventJson = """{
          "firstName":"Kamalpreet",
          "lastName":"Singh",
          "mobile":"9416923222",
            "identity" : {
              "deviceId": "5c723e5e-becf-4de7-8943-7c15a1b0f45a",
	          "sessionId": "e9298a5d-dca6-49c1-b333-f4eb6e7a2909",
	          "userId": "5a770a572ff9764dc8ff6a0d"
            },
          "gender":"M",
          "country":"India",
          "dob":"1994-10-11"
        }"""

        val requestEvent = RestAssured.given()
        requestEvent.body(validEventJson)
        requestEvent.contentType(ContentType.JSON)
        requestEvent.header("Authorization", token)
        val responseEvent = requestEvent.post("http://localhost:5454/push/profile")
        val bodyResponse = responseEvent.body()
        bodyResponse.print()
        responseEvent.then().statusCode(HttpStatus.SC_OK)
    }

    @Test
    fun invalidClientIdPushProfile() {

        val validEventJson = """{
          "firstName":"Kamalpreet",
          "lastName":"Singh",
          "mobile":"9416923222",
            "identity" : {
              "deviceId": "5c723e5e-becf-4de7-8943-7c15a1b0f45a",
	          "sessionId": "e9298a5d-dca6-49c1-b333-f4eb6e7a2909",
	          "userId": "5a770a572ff9764dc8ff6a0d"
            },
          "gender":"M",
          "country":"India",
          "dob":"1994-10-11",
          "clientId":"-56"
        }"""

        val requestEvent = RestAssured.given()
        requestEvent.body(validEventJson)
        requestEvent.contentType(ContentType.JSON)
        requestEvent.header("Authorization", token)
        val responseEvent = requestEvent.post("http://localhost:5454/push/profile")
        val bodyResponse = responseEvent.body()
        bodyResponse.print()
        responseEvent.then().statusCode(HttpStatus.SC_NOT_ACCEPTABLE)
    }

    @Test
    fun validClientIdPushProfile() {

        val validEventJson = """{
          "firstName":"Kamalpreet",
          "lastName":"Singh",
          "mobile":"9416923222",
            "identity" : {
              "deviceId": "5c723e5e-becf-4de7-8943-7c15a1b0f45a",
	          "sessionId": "e9298a5d-dca6-49c1-b333-f4eb6e7a2909",
	          "userId": "5a770a572ff9764dc8ff6a0d"
            },
          "gender":"M",
          "country":"India",
          "dob":"1994-10-11",
          "clientId":"256"
        }"""

        val requestEvent = RestAssured.given()
        requestEvent.body(validEventJson)
        requestEvent.contentType(ContentType.JSON)
        requestEvent.header("Authorization", token)
        val responseEvent = requestEvent.post("http://localhost:5454/push/profile")
        val bodyResponse = responseEvent.body()
        bodyResponse.print()
        responseEvent.then().statusCode(HttpStatus.SC_OK)
    }

    @Test
    fun invalidEmailIdPushProfile() {

        val validEventJson = """{
          "firstName":"Kamalpreet",
          "lastName":"Singh",
          "mobile":"9416923222",
            "identity" : {
              "deviceId": "5c723e5e-becf-4de7-8943-7c15a1b0f45a",
	          "sessionId": "e9298a5d-dca6-49c1-b333-f4eb6e7a2909",
	          "userId": "5a770a572ff9764dc8ff6a0d"
            },
          "gender":"M",
          "country":"India",
          "dob":"1994-10-11",
          "clientId":"256",
          "email":"fnbjsfvbbv"
        }"""

        val requestEvent = RestAssured.given()
        requestEvent.body(validEventJson)
        requestEvent.contentType(ContentType.JSON)
        requestEvent.header("Authorization", token)
        val responseEvent = requestEvent.post("http://localhost:5454/push/profile")
        val bodyResponse = responseEvent.body()
        bodyResponse.print()
        responseEvent.then().statusCode(HttpStatus.SC_NOT_ACCEPTABLE)
    }

    @Test
    fun validEmailIdEventPushProfile() {

        val validEventJson = """{
          "firstName":"Kamalpreet",
          "lastName":"Singh",
          "mobile":"9416923222",
            "identity" : {
              "deviceId": "5c723e5e-becf-4de7-8943-7c15a1b0f45a",
	          "sessionId": "e9298a5d-dca6-49c1-b333-f4eb6e7a2909",
	          "userId": "5a770a572ff9764dc8ff6a0d"
            },
          "gender":"M",
          "country":"India",
          "dob":"1994-10-11",
          "clientId":"256",
          "email":"kamalpreet.singh@userndot.com"
        }"""

        val requestEvent = RestAssured.given()
        requestEvent.body(validEventJson)
        requestEvent.contentType(ContentType.JSON)
        requestEvent.header("Authorization", token)
        val responseEvent = requestEvent.post("http://localhost:5454/push/profile")
        val bodyResponse = responseEvent.body()
        bodyResponse.print()
        responseEvent.then().statusCode(HttpStatus.SC_OK)
    }

    @Test
    fun invalidFbIdEventPushProfile() {

        val validEventJson = """{
          "firstName":"Kamalpreet",
          "lastName":"Singh",
          "mobile":"9416923222",
            "identity" : {
              "deviceId": "5c723e5e-becf-4de7-8943-7c15a1b0f45a",
	          "sessionId": "e9298a5d-dca6-49c1-b333-f4eb6e7a2909",
	          "userId": "5a770a572ff9764dc8ff6a0d"
            },
          "gender":"M",
          "country":"India",
          "dob":"1994-10-11",
          "clientId":"256",
          "fbId":"fnbjsfvbbv"
        }"""

        val requestEvent = RestAssured.given()
        requestEvent.body(validEventJson)
        requestEvent.contentType(ContentType.JSON)
        requestEvent.header("Authorization", token)
        val responseEvent = requestEvent.post("http://localhost:5454/push/profile")
        val bodyResponse = responseEvent.body()
        bodyResponse.print()
        responseEvent.then().statusCode(HttpStatus.SC_NOT_ACCEPTABLE)
    }

    @Test
    fun validFbIdEventPushProfile() {

        val validEventJson = """{
          "firstName":"Kamalpreet",
          "lastName":"Singh",
          "mobile":"9416923222",
            "identity" : {
              "deviceId": "5c723e5e-becf-4de7-8943-7c15a1b0f45a",
	          "sessionId": "e9298a5d-dca6-49c1-b333-f4eb6e7a2909",
	          "userId": "5a770a572ff9764dc8ff6a0d"
            },
          "gender":"M",
          "country":"India",
          "dob":"1994-10-11",
          "clientId":"256",
          "fbId":"brucewayne@gmail.com"
        }"""

        val requestEvent = RestAssured.given()
        requestEvent.body(validEventJson)
        requestEvent.contentType(ContentType.JSON)
        requestEvent.header("Authorization", token)
        val responseEvent = requestEvent.post("http://localhost:5454/push/profile")
        val bodyResponse = responseEvent.body()
        bodyResponse.print()
        responseEvent.then().statusCode(HttpStatus.SC_OK)
    }

    @Test
    fun invalidGoogleIdEventPushProfile() {

        val validEventJson = """{
          "firstName":"Kamalpreet",
          "lastName":"Singh",
          "mobile":"9416923222",
            "identity" : {
              "deviceId": "5c723e5e-becf-4de7-8943-7c15a1b0f45a",
	          "sessionId": "e9298a5d-dca6-49c1-b333-f4eb6e7a2909",
	          "userId": "5a770a572ff9764dc8ff6a0d"
            },
          "gender":"M",
          "country":"India",
          "dob":"1994-10-11",
          "clientId":"256",
          "googleId":"cndsjvbd"
        }"""

        val requestEvent = RestAssured.given()
        requestEvent.body(validEventJson)
        requestEvent.contentType(ContentType.JSON)
        requestEvent.header("Authorization", token)
        val responseEvent = requestEvent.post("http://localhost:5454/push/profile")
        val bodyResponse = responseEvent.body()
        bodyResponse.print()
        responseEvent.then().statusCode(HttpStatus.SC_NOT_ACCEPTABLE)
    }

    @Test
    fun validGoogleIdEventPushProfile() {

        val validEventJson = """{
          "firstName":"Kamalpreet",
          "lastName":"Singh",
          "mobile":"9416923222",
            "identity" : {
              "deviceId": "5c723e5e-becf-4de7-8943-7c15a1b0f45a",
	          "sessionId": "e9298a5d-dca6-49c1-b333-f4eb6e7a2909",
	          "userId": "5a770a572ff9764dc8ff6a0d"
            },
          "gender":"M",
          "country":"India",
          "dob":"1994-10-11",
          "clientId":"256",
          "googleId":"brucewayne@gmail.com"
        }"""

        val requestEvent = RestAssured.given()
        requestEvent.body(validEventJson)
        requestEvent.contentType(ContentType.JSON)
        requestEvent.header("Authorization", token)
        val responseEvent = requestEvent.post("http://localhost:5454/push/profile")
        val bodyResponse = responseEvent.body()
        bodyResponse.print()
        responseEvent.then().statusCode(HttpStatus.SC_OK)
    }

    @Test
    fun invalidCountryCodeEventPushProfile() {

        val validEventJson = """{
          "firstName":"Kamalpreet",
          "lastName":"Singh",
          "mobile":"9416923222",
            "identity" : {
              "deviceId": "5c723e5e-becf-4de7-8943-7c15a1b0f45a",
	          "sessionId": "e9298a5d-dca6-49c1-b333-f4eb6e7a2909",
	          "userId": "5a770a572ff9764dc8ff6a0d"
            },
          "gender":"M",
          "country":"India",
          "dob":"1994-10-11",
          "clientId":"256",
          "countryCode":"+12231616111366"
        }"""

        val requestEvent = RestAssured.given()
        requestEvent.body(validEventJson)
        requestEvent.contentType(ContentType.JSON)
        requestEvent.header("Authorization", token)
        val responseEvent = requestEvent.post("http://localhost:5454/push/profile")
        val bodyResponse = responseEvent.body()
        bodyResponse.print()
        responseEvent.then().statusCode(HttpStatus.SC_NOT_ACCEPTABLE)
    }

    @Test
    fun validCountryCodePushProfile() {

        val validEventJson = """{
          "firstName":"Kamalpreet",
          "lastName":"Singh",
          "mobile":"9416923222",
            "identity" : {
              "deviceId": "5c723e5e-becf-4de7-8943-7c15a1b0f45a",
	          "sessionId": "e9298a5d-dca6-49c1-b333-f4eb6e7a2909",
	          "userId": "5a770a572ff9764dc8ff6a0d"
            },
          "gender":"M",
          "country":"India",
          "dob":"1994-10-11",
          "clientId":"256",
          "countryCode":"+154"
        }"""

        val requestEvent = RestAssured.given()
        requestEvent.body(validEventJson)
        requestEvent.contentType(ContentType.JSON)
        requestEvent.header("Authorization", token)
        val responseEvent = requestEvent.post("http://localhost:5454/push/profile")
        val bodyResponse = responseEvent.body()
        bodyResponse.print()
        responseEvent.then().statusCode(HttpStatus.SC_OK)
    }

    @Test
    fun test(){

        val validEventJson = """{
          "firstName":"Kamalpreet",
          "lastName":"Singh",
          "country":"India",
          "clientId":"256"
        }"""

        val requestEvent = RestAssured.given()
        requestEvent.body(validEventJson)
        requestEvent.contentType(ContentType.JSON)
        requestEvent.header("Authorization", token)
        val responseEvent = requestEvent.post("http://localhost:5454/push/profile")
        val bodyResponse = responseEvent.body()
        bodyResponse.print()
        responseEvent.then().statusCode(HttpStatus.SC_OK)
        val userId = responseEvent.jsonPath().get<String>("data.value.userId")
        val deviceId=responseEvent.jsonPath().get<String>("data.value.deviceId")
        val sessionId=responseEvent.jsonPath().get<String>("data.value.sessionId")

        val updatedEventJson = """{
          "mobile":"9416923222",
          "gender":"M",
          "identity" : {
              "deviceId": "$deviceId",
	          "sessionId": "$sessionId",
	          "userId": "$userId"
            },
          "clientId":"512"
        }"""

        val updatedRequestEvent = RestAssured.given()
        updatedRequestEvent.body(updatedEventJson)
        updatedRequestEvent.contentType(ContentType.JSON)
        updatedRequestEvent.header("Authorization", token)
        val updatedResponseEvent = updatedRequestEvent.post("http://localhost:5454/push/profile")
        val updatedBodyResponse = updatedResponseEvent.body()
        updatedBodyResponse.print()
        updatedResponseEvent.then().statusCode(HttpStatus.SC_OK)

        val updatedEventJson2 = """{
          "identity" : {
              "deviceId": "$deviceId",
	          "sessionId": "$sessionId",
	          "userId": "$userId"
            },
          "dob":"1994-10-11"
        }"""

        val updatedRequestEvent2 = RestAssured.given()
        updatedRequestEvent2.body(updatedEventJson2)
        updatedRequestEvent2.contentType(ContentType.JSON)
        updatedRequestEvent2.header("Authorization", token)
        val updatedResponseEvent2= updatedRequestEvent2.post("http://localhost:5454/push/profile")
        val updatedBodyResponse2 = updatedResponseEvent2.body()
        updatedBodyResponse2.print()
        updatedResponseEvent2.then().statusCode(HttpStatus.SC_OK)

    }
//Kamalpreet
}