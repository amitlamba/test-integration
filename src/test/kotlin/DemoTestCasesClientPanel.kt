import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.und.model.web.EmailTemplate
import com.und.model.web.*
import com.und.repository.CampaignRepository
import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.specification.RequestSpecification
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.test.context.junit4.SpringRunner
import java.io.File
import java.io.StringReader
import java.util.*
import javax.persistence.EntityManager

@DataJpaTest
@EnableJpaRepositories(basePackages = [ "com.und.repository" ])
open class DemoTestCasesClientPanel {

    @Autowired
    private lateinit var entityManager: EntityManager

    @Autowired
    private lateinit var campaignRepository: CampaignRepository

    private var token = ""                      //Variable to hold Authorization token
    private val userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36"
    private lateinit var objectMapper: ObjectMapper
    private val profile = "dev"

    private var campaignId = 0
    private val id = 34

    private lateinit var authUrl: String

    private lateinit var campaignListUrl: String
    private lateinit var saveCampaignUrl: String
    private lateinit var pauseCampaignUrl: String
    private lateinit var resumeCampaignUrl: String
    private lateinit var stopCampaignUrl: String
    private lateinit var deleteCampaignUrl: String

    private lateinit var trackLinkUrl: String
    private lateinit var createTrackLinkUrl: String

    private lateinit var clientUsersListUrl: String
    private lateinit var clientUsersUserEventsUrl: String

    private lateinit var saveContactUsUrl: String

    private lateinit var defaultEmailTemplatesUrl: String
    private lateinit var clientEmailTemplatesUrl: String
    private lateinit var saveEmailTemplateUrl: String
    private lateinit var emailTemplateUserEventAttributesUrl: String

    private lateinit var countriesListUrl: String
    private lateinit var statesByCountryIDUrl: String
    private lateinit var citiesByStateIDUrl: String

    private lateinit var segmentMetaDataUrl: String
    private lateinit var segmentCommonPropertiesUrl: String
    private lateinit var saveSegmentUrl: String
    private lateinit var segmentListUrl: String

    private lateinit var defaultSmsTemplateUrl: String
    private lateinit var clientSmsTemplatesUrl: String
    private lateinit var saveSmsTemplateUrl: String
    private lateinit var smsTemplateUserEventAttributes: String

    private lateinit var serviceProviderUrl: String
    private lateinit var emailServiceProvidersUrl: String
    private lateinit var emailServiceProviderUrl: String
    private lateinit var saveEmailServiceProviderUrl: String
    private lateinit var smsServiceProvidersUrl: String
    private lateinit var smsServiceProviderUrl: String
    private lateinit var saveSmsServiceProviderUrl: String
    private lateinit var addSendersEmailUrl: String
    private lateinit var sendersEmailListUrl: String
    private lateinit var deleteSendersEmailUrl: String
    private lateinit var saveAccountSettingsUrl: String
    private lateinit var accountSettingsUrl: String
    private lateinit var unsubscribeLinkUrl: String
    private lateinit var saveUnsubscribeLinkUrl: String


    @Before
    fun runBeforeClass() {

        val properties = Properties()
        properties.load(StringReader(readFileText("profiles/$profile.properties")))

        authUrl = properties.getProperty("authUrl")

        campaignListUrl = properties.getProperty("campaignListUrl")
        saveCampaignUrl = properties.getProperty("saveCampaignUrl")
        pauseCampaignUrl = properties.getProperty("saveCampaignUrl")
        resumeCampaignUrl = properties.getProperty("saveCampaignUrl")
        stopCampaignUrl = properties.getProperty("saveCampaignUrl")
        deleteCampaignUrl = properties.getProperty("deleteCampaignUrl")

        trackLinkUrl = properties.getProperty("trackLinkUrl")
        createTrackLinkUrl = properties.getProperty("createTrackLinkUrl")

        clientUsersListUrl = properties.getProperty("clientUsersListUrl")
        clientUsersUserEventsUrl = properties.getProperty("clientUsersEventsUrl")

        saveContactUsUrl = properties.getProperty("saveContactUsUrl")

        defaultEmailTemplatesUrl = properties.getProperty("defaultEmailTemplatesUrl")
        clientEmailTemplatesUrl = properties.getProperty("clientEmailTemplatesUrl")
        saveEmailTemplateUrl = properties.getProperty("saveEmailTemplateUrl")
        emailTemplateUserEventAttributesUrl = properties.getProperty("emailTemplateUserEventAttributesUrl")

        countriesListUrl = properties.getProperty("countriesListUrl")
        statesByCountryIDUrl = properties.getProperty("statesByCountryIDUrl")
        citiesByStateIDUrl = properties.getProperty("citiesByStateIDUrl")

        segmentMetaDataUrl = properties.getProperty("segmentMetaDataUrl")
        segmentCommonPropertiesUrl = properties.getProperty("segmentCommonPropertiesUrl")
        saveSegmentUrl = properties.getProperty("saveSegmentUrl")
        segmentListUrl = properties.getProperty("segmentListUrl")

        defaultSmsTemplateUrl = properties.getProperty("defaultSmsTemplateUrl")
        clientSmsTemplatesUrl = properties.getProperty("clientSmsTemplatesUrl")
        saveSmsTemplateUrl = properties.getProperty("saveSmsTemplateUrl")
        smsTemplateUserEventAttributes = properties.getProperty("smsTemplateUserEventAttributes")

        serviceProviderUrl = properties.getProperty("serviceProviderUrl")
        emailServiceProvidersUrl = properties.getProperty("emailServiceProvidersUrl")
        emailServiceProviderUrl = properties.getProperty("emailServiceProviderUrl")
        saveEmailServiceProviderUrl = properties.getProperty("saveEmailServiceProviderUrl")
        smsServiceProvidersUrl = properties.getProperty("smsServiceProvidersUrl")
        smsServiceProviderUrl = properties.getProperty("smsServiceProviderUrl")
        saveSmsServiceProviderUrl = properties.getProperty("saveSmsServiceProviderUrl")
        addSendersEmailUrl = properties.getProperty("addSendersEmailUrl")
        sendersEmailListUrl = properties.getProperty("sendersEmailListUrl")
        deleteSendersEmailUrl = properties.getProperty("deleteSendersEmailUrl")
        saveAccountSettingsUrl = properties.getProperty("saveAccountSettingsUrl")
        accountSettingsUrl = properties.getProperty("accountSettingsUrl")
        unsubscribeLinkUrl = properties.getProperty("unsubscribeLinkUrl")
        saveUnsubscribeLinkUrl = properties.getProperty("saveUnsubscribeLinkUrl")

        objectMapper = objectMapper()

        //Generating Token
        val loginDetails = """{"username":"kamalpreetsingh025@gmail.com","password":"Kamalpreet123!"}"""
        val request = requestSpecificationWithOutAuth(loginDetails)
        val response = request.post(authUrl)
        token = response.jsonPath().get<String>("data.value.token")

    }


    @Test
    fun testSaveEmailTemplateWithAuthToken() {
        val campaign=campaignRepository.findByClientID(17)
        print(campaign)


    }















    private fun requestSpecificationWithAuth(body: String, token: String): RequestSpecification {
        val requestEvent = RestAssured.given()
        requestEvent.body(body)
        requestEvent.contentType(ContentType.JSON)
        requestEvent.header("Authorization", token)
        requestEvent.header("User-Agent", userAgent)
        return requestEvent
    }

    private fun requestSpecificationWithAuth(token: String): RequestSpecification {
        val requestEvent = RestAssured.given()
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

    private fun requestSpecificationWithOutAuth(): RequestSpecification {
        val request = RestAssured.given()
        request.contentType(ContentType.JSON)
        request.header("User-Agent", userAgent)
        return request
    }

    private fun readFileText(fileName: String): String {
        val classLoader = ClassLoader.getSystemClassLoader()
        val file = File(classLoader.getResource(fileName).file)
        return file.readText(Charsets.UTF_8)
    }

    private fun buildCampaign(file: String): String {

        val eventJson = readFileText(file)
        val event = objectMapper.readValue(eventJson, Campaign::class.java)
        return objectMapper.writeValueAsString(event)

    }

    private fun buildUnsubscribeLink(file: String): String {

        val eventJson = readFileText(file)
        val event = objectMapper.readValue(eventJson, UnSubscribeLink::class.java)
        return objectMapper.writeValueAsString(event)

    }

    private fun buildAccountSettings(file: String): String {

        val eventJson = readFileText(file)
        val event = objectMapper.readValue(eventJson, AccountSettings::class.java)
        return objectMapper.writeValueAsString(event)

    }

    private fun buildSendersEmail(file: String): String {

        val eventJson = readFileText(file)
        val event = objectMapper.readValue(eventJson, SendersInfo::class.java)
        return objectMapper.writeValueAsString(event)

    }

    private fun buildEmailServiceProvider(file: String): String {

        val eventJson = readFileText(file)
        val event = objectMapper.readValue(eventJson, ServiceProviderCredentials::class.java)
        return objectMapper.writeValueAsString(event)

    }

    private fun buildEmailTemplate(file: String): String {

        val eventJson = readFileText(file)
        val event = objectMapper.readValue(eventJson, EmailTemplate::class.java)
        return objectMapper.writeValueAsString(event)

    }

    private fun buildContactUs(file: String): String {

        val eventJson = readFileText(file)
        val event = objectMapper.readValue(eventJson, ContactUs::class.java)
        return objectMapper.writeValueAsString(event)

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