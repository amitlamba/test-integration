import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.und.model.web.EmailTemplate
import com.und.model.web.*
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


class AuthenticationTestCasesClientPanel {

    private var token = ""                      //Variable to hold Authorization token
    private val userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36"
    private lateinit var objectMapper: ObjectMapper
    private val profile = "dev"

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
    private lateinit var clientUsersEventsUrl: String

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

    private var campaignId = 0
    private val countryId = 101
    private val stateId = 13
    private val id = null

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
        clientUsersEventsUrl = properties.getProperty("clientUsersEventsUrl")

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
    fun testGetCampaignListWithAuthToken() {
        val requestEvent = requestSpecificationWithAuth(token)
        val responseEvent = requestEvent.get(campaignListUrl)
        assertEquals(HttpStatus.SC_OK, responseEvent.statusCode)
    }

    @Test
    fun testGetCampaignListWithoutAuthToken() {
        val requestEvent = requestSpecificationWithOutAuth()
        val responseEvent = requestEvent.get(campaignListUrl)
        assertEquals(HttpStatus.SC_UNAUTHORIZED, responseEvent.statusCode)
    }

    @Test
    fun testGetCampaignListWithIncorrectAuthToken() {
        val incorrectToken = RandomStringUtils.random(256, true, true)
        val requestEvent = requestSpecificationWithAuth(incorrectToken)
        val responseEvent = requestEvent.get(campaignListUrl)
        assertEquals(HttpStatus.SC_UNAUTHORIZED, responseEvent.statusCode)
    }

    /*@Test
    fun testSaveCampaignWithAuthToken() {
        val userProfileJson = buildCampaign("campaign.json")
        val requestEvent = requestSpecificationWithAuth(userProfileJson, token)
        val responseEvent = requestEvent.post(saveCampaignUrl)
        assertEquals(HttpStatus.SC_CREATED, responseEvent.statusCode)
        campaignId = responseEvent.jsonPath().get<Int>("id")
    }

    @Test
    fun testSaveCampaignWithoutAuthToken() {
        val userProfileJson = buildCampaign("campaign.json")
        val requestEvent = requestSpecificationWithOutAuth(userProfileJson)
        val responseEvent = requestEvent.post(saveCampaignUrl)
        assertEquals(HttpStatus.SC_UNAUTHORIZED, responseEvent.statusCode)
    }

    @Test
    fun testSaveCampaignWithIncorrectAuthToken() {
        val userProfileJson = buildCampaign("campaign.json")
        val incorrectToken = RandomStringUtils.random(256, true, true)
        val requestEvent = requestSpecificationWithAuth(userProfileJson, incorrectToken)
        val responseEvent = requestEvent.post(saveCampaignUrl)
        assertEquals(HttpStatus.SC_UNAUTHORIZED, responseEvent.statusCode)
    }

    @Test
    fun testPauseCampaignWithAuthToken() {
        val userProfileJson = """{"campaignId":"$campaignId"}"""
        val requestEvent = requestSpecificationWithAuth(userProfileJson, token)
        val responseEvent = requestEvent.patch(pauseCampaignUrl.plus("/$campaignId"))
        assertEquals(HttpStatus.SC_OK, responseEvent.statusCode)
    }

    @Test
    fun testPauseCampaignWithoutAuthToken() {
        val userProfileJson = """{"campaignId":"$campaignId"}"""
        val requestEvent = requestSpecificationWithOutAuth(userProfileJson)
        val responseEvent = requestEvent.patch(pauseCampaignUrl.plus("/$campaignId"))
        assertEquals(HttpStatus.SC_UNAUTHORIZED, responseEvent.statusCode)
    }

    @Test
    fun testResumeCampaignWithAuthToken() {
        val userProfileJson = """{"campaignId":"$campaignId"}"""
        val requestEvent = requestSpecificationWithAuth(userProfileJson, token)
        val responseEvent = requestEvent.patch(resumeCampaignUrl.plus("/$campaignId"))
        assertEquals(HttpStatus.SC_OK, responseEvent.statusCode)
    }

    @Test
    fun testResumeCampaignWithoutAuthToken() {
        val userProfileJson = """{"campaignId":"$campaignId"}"""
        val requestEvent = requestSpecificationWithOutAuth(userProfileJson)
        val responseEvent = requestEvent.patch(resumeCampaignUrl.plus("/$campaignId"))
        assertEquals(HttpStatus.SC_UNAUTHORIZED, responseEvent.statusCode)
    }

    @Test
    fun testStopCampaignWithAuthToken() {
        val userProfileJson = """{"campaignId":"$campaignId"}"""
        val requestEvent = requestSpecificationWithAuth(userProfileJson, token)
        val responseEvent = requestEvent.patch(stopCampaignUrl.plus("/$campaignId"))
        assertEquals(HttpStatus.SC_OK, responseEvent.statusCode)
    }

    @Test
    fun testStopCampaignWithoutAuthToken() {
        val userProfileJson = """{"campaignId":"$campaignId"}"""
        val requestEvent = requestSpecificationWithOutAuth(userProfileJson)
        val responseEvent = requestEvent.patch(stopCampaignUrl.plus("/$campaignId"))
        assertEquals(HttpStatus.SC_UNAUTHORIZED, responseEvent.statusCode)
    }

    @Test
    fun testDeleteCampaignWithAuthToken() {
        val userProfileJson = """{"campaignId":"$campaignId"}"""
        val requestEvent = requestSpecificationWithAuth(userProfileJson, token)
        val responseEvent = requestEvent.patch(deleteCampaignUrl.plus("/$campaignId"))
        assertEquals(HttpStatus.SC_OK, responseEvent.statusCode)
    }

    @Test
    fun testDeleteCampaignWithoutAuthToken() {
        //FIXME It should be unauthorized but is bad request
        val userProfileJson = """{"campaignId":"$campaignId"}"""
        val requestEvent = requestSpecificationWithOutAuth(userProfileJson)
        val responseEvent = requestEvent.patch(deleteCampaignUrl.plus("/$campaignId"))
        assertEquals(HttpStatus.SC_UNAUTHORIZED, responseEvent.statusCode)
    }*/

    /*@Test
    fun tesGetTrackLinkWithAuthToken() {
        //FIXME Url missing
        val requestEvent = requestSpecificationWithAuth(token)
        val responseEvent = requestEvent.get(trackLinkUrl)
        assertEquals(HttpStatus.SC_OK, responseEvent.statusCode)

    }

    @Test
    fun testGetTrackLinkWithoutAuthToken() {
        //FIXME Url missing
        val requestEvent = requestSpecificationWithOutAuth()
        val responseEvent = requestEvent.get(trackLinkUrl)
        assertEquals(HttpStatus.SC_UNAUTHORIZED, responseEvent.statusCode)

    }

    @Test
    fun tesCreateTrackLinkWithAuthToken() {
        //FIXME Url missing
        val requestEvent = requestSpecificationWithAuth(token)
        val responseEvent = requestEvent.get(createTrackLinkUrl)
        assertEquals(HttpStatus.SC_OK, responseEvent.statusCode)

    }

    @Test
    fun testCreateTrackLinkWithoutAuthToken() {
        //FIXME Url missing
        val requestEvent = requestSpecificationWithOutAuth()
        val responseEvent = requestEvent.get(createTrackLinkUrl)
        assertEquals(HttpStatus.SC_UNAUTHORIZED, responseEvent.statusCode)

    }*/

    @Test
    fun testGetClientUsersListWithAuthToken() {

        val requestEvent = requestSpecificationWithAuth(token)
        val responseEvent = requestEvent.get(clientUsersListUrl)
        assertEquals(HttpStatus.SC_OK, responseEvent.statusCode)

    }

    @Test
    fun testGetClientUsersListWithoutAuthToken() {

        val requestEvent = requestSpecificationWithOutAuth()
        val responseEvent = requestEvent.get(clientUsersListUrl)
        assertEquals(HttpStatus.SC_UNAUTHORIZED, responseEvent.statusCode)

    }

    /* @Test
     fun testGetClientUsersEventsWithAuthToken() {

         //FIXME Method Incomplete
         val requestEvent = requestSpecificationWithAuth(token)
         val responseEvent = requestEvent.get(clientUsersEventsUrl)
         assertEquals(HttpStatus.SC_OK, responseEvent.statusCode)

     }

     @Test
     fun testGetClientUsersEventsWithoutAuthToken() {
         //FIXME Method Incomplete
         val requestEvent = requestSpecificationWithOutAuth()
         val responseEvent = requestEvent.get(clientUsersEventsUrl)
         assertEquals(HttpStatus.SC_UNAUTHORIZED, responseEvent.statusCode)

     }*/

    /* @Test
     fun testSaveContactUsWithAuthToken() {
         //FIXME Already registered
         val contactUsJson = buildContactUs("contactUs.json")
         val requestEvent = requestSpecificationWithAuth(contactUsJson, token)
         val responseEvent = requestEvent.post(saveContactUsUrl)
         assertEquals(HttpStatus.SC_OK, responseEvent.statusCode)
     }*/

    @Test
    fun testSaveContactUsWithoutAuthToken() {
        val contactUsJson = buildContactUs("contactUs.json")
        val requestEvent = requestSpecificationWithOutAuth(contactUsJson)
        val responseEvent = requestEvent.post(saveContactUsUrl)
        assertEquals(HttpStatus.SC_UNAUTHORIZED, responseEvent.statusCode)
    }

    @Test
    fun testGetDefaultEmailTemplatesWithAuthToken() {

        val requestEvent = requestSpecificationWithAuth(token)
        val responseEvent = requestEvent.get(defaultEmailTemplatesUrl)
        assertEquals(HttpStatus.SC_OK, responseEvent.statusCode)

    }

    @Test
    fun testGetDefaultEmailTemplatesWithoutAuthToken() {

        val requestEvent = requestSpecificationWithOutAuth()
        val responseEvent = requestEvent.get(defaultEmailTemplatesUrl)
        assertEquals(HttpStatus.SC_UNAUTHORIZED, responseEvent.statusCode)

    }

    @Test
    fun testGetClientEmailTemplatesWithAuthToken() {

        val requestEvent = requestSpecificationWithAuth(token)
        val responseEvent = requestEvent.get(clientEmailTemplatesUrl)
        assertEquals(HttpStatus.SC_OK, responseEvent.statusCode)

    }

    @Test
    fun testGetClientEmailTemplatesWithoutAuthToken() {

        val requestEvent = requestSpecificationWithOutAuth()
        val responseEvent = requestEvent.get(clientEmailTemplatesUrl)
        assertEquals(HttpStatus.SC_UNAUTHORIZED, responseEvent.statusCode)

    }

    @Test
    fun testSaveEmailTemplateWithAuthToken() {

        val saveEmailTemplateJson = buildEmailTemplate("emailTemplate.json")
        val requestEvent = requestSpecificationWithAuth(saveEmailTemplateJson, token)
        val responseEvent = requestEvent.post(saveEmailTemplateUrl)
        assertEquals(HttpStatus.SC_OK, responseEvent.statusCode)

    }

    @Test
    fun testSaveEmailTemplateWithoutAuthToken() {

        val saveEmailTemplateJson = buildEmailTemplate("emailTemplate.json")
        val requestEvent = requestSpecificationWithOutAuth(saveEmailTemplateJson)
        val responseEvent = requestEvent.post(saveEmailTemplateUrl)
        assertEquals(HttpStatus.SC_UNAUTHORIZED, responseEvent.statusCode)

    }

    /*@Test
    fun testGetEmailTemplatesUserEventAttributesWithAuthToken() {
        //FIXME Not Implemented
        val requestEvent = requestSpecificationWithAuth(token)
        val responseEvent = requestEvent.get(emailTemplateUserEventAttributesUrl)
        assertEquals(HttpStatus.SC_OK, responseEvent.statusCode)

    }*/

    @Test
    fun testGetEmailTemplatesUserEventAttributesWithoutAuthToken() {

        val requestEvent = requestSpecificationWithOutAuth()
        val responseEvent = requestEvent.get(emailTemplateUserEventAttributesUrl)
        assertEquals(HttpStatus.SC_UNAUTHORIZED, responseEvent.statusCode)

    }

    @Test
    fun testGetCountriesListWithAuthToken() {

        val requestEvent = requestSpecificationWithAuth(token)
        val responseEvent = requestEvent.get(countriesListUrl)
        assertEquals(HttpStatus.SC_OK, responseEvent.statusCode)

    }

    @Test
    fun testGetCountriesListWithoutAuthToken() {

        val requestEvent = requestSpecificationWithOutAuth()
        val responseEvent = requestEvent.get(countriesListUrl)
        assertEquals(HttpStatus.SC_UNAUTHORIZED, responseEvent.statusCode)

    }

    @Test
    fun testGetStatesByCountryIDWithAuthToken() {

        val requestEvent = requestSpecificationWithAuth(token)
        val responseEvent = requestEvent.get(statesByCountryIDUrl.plus("/$countryId"))
        assertEquals(HttpStatus.SC_OK, responseEvent.statusCode)

    }

     /*@Test
     fun testGetStatesByCountryIDWithoutAuthToken() {
         //FIXME It should be Unauthorized but is Bad Request
         val requestEvent = requestSpecificationWithOutAuth()
         val responseEvent = requestEvent.get(statesByCountryIDUrl.plus("/$countryId"))
         assertEquals(HttpStatus.SC_UNAUTHORIZED, responseEvent.statusCode)

     }*/

    @Test
    fun testGetCitiesByStateIDWithAuthToken() {

        val requestEvent = requestSpecificationWithAuth(token)
        val responseEvent = requestEvent.get(citiesByStateIDUrl.plus("/$stateId"))
        assertEquals(HttpStatus.SC_OK, responseEvent.statusCode)

    }

    /*@Test
    fun testGetCitiesByStateIDWithoutAuthToken() {
        //FIXME it should be Unauthorized but it is Bad Request
        val requestEvent = requestSpecificationWithOutAuth()
        val responseEvent = requestEvent.get(citiesByStateIDUrl.plus("/$stateId"))
        assertEquals(HttpStatus.SC_UNAUTHORIZED, responseEvent.statusCode)

    }*/

    @Test
    fun testGetSegmentMetadataWithAuthToken() {

        val requestEvent = requestSpecificationWithAuth(token)
        val responseEvent = requestEvent.get(segmentMetaDataUrl)
        assertEquals(HttpStatus.SC_OK, responseEvent.statusCode)

    }

    @Test
    fun testGetSegmentMetadataWithoutAuthToken() {

        val requestEvent = requestSpecificationWithOutAuth()
        val responseEvent = requestEvent.get(segmentMetaDataUrl)
        assertEquals(HttpStatus.SC_UNAUTHORIZED, responseEvent.statusCode)

    }

    @Test
    fun testGetSegmentCommonPropertiesWithAuthToken() {

        val requestEvent = requestSpecificationWithAuth(token)
        val responseEvent = requestEvent.get(segmentCommonPropertiesUrl)
        assertEquals(HttpStatus.SC_OK, responseEvent.statusCode)

    }

    @Test
    fun testGetSegmentCommonPropertiesWithoutAuthToken() {

        val requestEvent = requestSpecificationWithOutAuth()
        val responseEvent = requestEvent.get(segmentCommonPropertiesUrl)
        assertEquals(HttpStatus.SC_UNAUTHORIZED, responseEvent.statusCode)

    }

    @Test
    fun testSaveSegmentWithAuthToken() {

        val saveSegmentJson = buildCampaign("segment.json")
        val requestEvent = requestSpecificationWithAuth(saveSegmentJson, token)
        val responseEvent = requestEvent.post(saveSegmentUrl)
        assertEquals(HttpStatus.SC_CREATED, responseEvent.statusCode)

    }

    @Test
    fun testSaveSegmentWithoutAuthToken() {

        val saveSegmentJson = buildCampaign("segment.json")
        val requestEvent = requestSpecificationWithOutAuth(saveSegmentJson)
        val responseEvent = requestEvent.post(saveSegmentUrl)
        assertEquals(HttpStatus.SC_UNAUTHORIZED, responseEvent.statusCode)

    }

    @Test
    fun testGetSegmentListWithAuthToken() {

        val requestEvent = requestSpecificationWithAuth(token)
        val responseEvent = requestEvent.get(segmentListUrl)
        assertEquals(HttpStatus.SC_OK, responseEvent.statusCode)

    }

    @Test
    fun testGetSegmentListWithoutAuthToken() {

        val requestEvent = requestSpecificationWithOutAuth()
        val responseEvent = requestEvent.get(segmentListUrl)
        assertEquals(HttpStatus.SC_UNAUTHORIZED, responseEvent.statusCode)

    }

    @Test
    fun testGetServiceProviderWithAuthToken() {

        val requestEvent = requestSpecificationWithAuth(token)
        val responseEvent = requestEvent.get(serviceProviderUrl)
        assertEquals(HttpStatus.SC_OK, responseEvent.statusCode)

    }

    @Test
    fun testGetServiceProviderWithoutAuthToken() {

        val requestEvent = requestSpecificationWithOutAuth()
        val responseEvent = requestEvent.get(serviceProviderUrl)
        assertEquals(HttpStatus.SC_UNAUTHORIZED, responseEvent.statusCode)

    }

    @Test
    fun testSaveEmailServiceProviderWithAuthToken() {

        val emailServiceProviderJson = buildEmailServiceProvider("emailServiceProvider.json")
        val requestEvent = requestSpecificationWithAuth(emailServiceProviderJson, token)
        val responseEvent = requestEvent.post(saveEmailServiceProviderUrl)
        assertEquals(HttpStatus.SC_OK, responseEvent.statusCode)

    }

    @Test
    fun testSaveEmailServiceProviderWithoutAuthToken() {

        val emailServiceProviderJson = buildEmailServiceProvider("emailServiceProvider.json")
        val requestEvent = requestSpecificationWithOutAuth(emailServiceProviderJson)
        val responseEvent = requestEvent.post(saveEmailServiceProviderUrl)
        assertEquals(HttpStatus.SC_UNAUTHORIZED, responseEvent.statusCode)

    }

    @Test
    fun testGetEmailServiceProvidersWithAuthToken() {

        val requestEvent = requestSpecificationWithAuth(token)
        val responseEvent = requestEvent.get(emailServiceProvidersUrl)
        assertEquals(HttpStatus.SC_OK, responseEvent.statusCode)

    }

    @Test
    fun testGetEmailServiceProvidersWithoutAuthToken() {

        val requestEvent = requestSpecificationWithOutAuth()
        val responseEvent = requestEvent.get(emailServiceProvidersUrl)
        assertEquals(HttpStatus.SC_UNAUTHORIZED, responseEvent.statusCode)

    }

    @Test
    fun testGetEmailServiceProviderWithAuthToken() {

        val requestEvent = requestSpecificationWithAuth(token)
        val responseEvent = requestEvent.get(emailServiceProviderUrl.plus("/$id"))
        assertEquals(HttpStatus.SC_OK, responseEvent.statusCode)

    }

    @Test
    fun testGetEmailServiceProviderWithoutAuthToken() {

        val requestEvent = requestSpecificationWithOutAuth()
        val responseEvent = requestEvent.get(emailServiceProviderUrl.plus("/$id"))
        assertEquals(HttpStatus.SC_UNAUTHORIZED, responseEvent.statusCode)

    }

    /*@Test
    fun testGetDefaultSmsTemplatesWithAuthToken() {

        val requestEvent = requestSpecificationWithAuth(token)
        val responseEvent = requestEvent.get(defaultSmsTemplateUrl)
        assertEquals(HttpStatus.SC_OK, responseEvent.statusCode)

    }

    @Test
    fun testGetDefaultSmsTemplatesWithoutAuthToken() {

        val requestEvent = requestSpecificationWithOutAuth()
        val responseEvent = requestEvent.get(defaultSmsTemplateUrl)
        assertEquals(HttpStatus.SC_UNAUTHORIZED, responseEvent.statusCode)

    }

    @Test
    fun testGetClientSmsTemplatesWithAuthToken() {

        val requestEvent = requestSpecificationWithAuth(token)
        val responseEvent = requestEvent.get(clientSmsTemplatesUrl)
        assertEquals(HttpStatus.SC_OK, responseEvent.statusCode)

    }

    @Test
    fun testGetClientSmsTemplatesWithoutAuthToken() {

        val requestEvent = requestSpecificationWithOutAuth()
        val responseEvent = requestEvent.get(clientSmsTemplatesUrl)
        assertEquals(HttpStatus.SC_UNAUTHORIZED, responseEvent.statusCode)

    }

    @Test
    fun testSaveSmsTemplateWithAuthToken() {

        val smsTemplateJson = buildCampaign("smsTemplate.json")
        val requestEvent = requestSpecificationWithAuth(smsTemplateJson,token)
        val responseEvent = requestEvent.post(saveSmsTemplateUrl)
        assertEquals(HttpStatus.SC_OK, responseEvent.statusCode)

    }

    @Test
    fun testSaveSmsTemplateWithoutAuthToken() {

        val smsTemplateJson = buildCampaign("smsTemplate.json")
        val requestEvent = requestSpecificationWithOutAuth(smsTemplateJson)
        val responseEvent = requestEvent.post(saveSmsTemplateUrl)
        assertEquals(HttpStatus.SC_UNAUTHORIZED, responseEvent.statusCode)

    }

    @Test
    fun testGetSmsTemplateEventAttributesWithAuthToken() {

        val requestEvent = requestSpecificationWithAuth(token)
        val responseEvent = requestEvent.get(smsTemplateUserEventAttributes)
        assertEquals(HttpStatus.SC_OK, responseEvent.statusCode)

    }

    @Test
    fun testGetSmsTemplateEventAttributesWithoutAuthToken() {

        val requestEvent = requestSpecificationWithOutAuth()
        val responseEvent = requestEvent.get(smsTemplateUserEventAttributes)
        assertEquals(HttpStatus.SC_UNAUTHORIZED, responseEvent.statusCode)

    }

    @Test
    fun testGetSmsServiceProvidersWithAuthToken() {
        //Sms is not yet completed
        val requestEvent = requestSpecificationWithAuth(token)
        val responseEvent = requestEvent.get(smsServiceProvidersUrl)
        assertEquals(HttpStatus.SC_OK, responseEvent.statusCode)

    }

    @Test
    fun testGetSmsServiceProvidersWithoutAuthToken() {

        val requestEvent = requestSpecificationWithOutAuth()
        val responseEvent = requestEvent.get(smsServiceProvidersUrl)
        assertEquals(HttpStatus.SC_UNAUTHORIZED, responseEvent.statusCode)

    }

    @Test
    fun testGetSmsServiceProviderWithAuthToken() {

        val requestEvent = requestSpecificationWithAuth(token)
        val responseEvent = requestEvent.get(smsServiceProviderUrl)
        assertEquals(HttpStatus.SC_OK, responseEvent.statusCode)

    }

    @Test
    fun testGetSmsServiceProviderWithoutAuthToken() {

        val requestEvent = requestSpecificationWithOutAuth()
        val responseEvent = requestEvent.get(smsServiceProviderUrl)
        assertEquals(HttpStatus.SC_UNAUTHORIZED, responseEvent.statusCode)

    }

    @Test
    fun testSaveSmsServiceProviderWithAuthToken() {

        val smsServiceProviderJson = buildCampaign("smsServiceProvider.json")
        val requestEvent = requestSpecificationWithAuth(smsServiceProviderJson, token)
        val responseEvent = requestEvent.post(saveSmsServiceProviderUrl)
        assertEquals(HttpStatus.SC_OK, responseEvent.statusCode)

    }

    @Test
    fun testSaveSmsServiceProviderWithoutAuthToken() {

        val smsServiceProviderJson = buildCampaign("smsServiceProvider.json")
        val requestEvent = requestSpecificationWithOutAuth(smsServiceProviderJson)
        val responseEvent = requestEvent.post(saveSmsServiceProviderUrl)
        assertEquals(HttpStatus.SC_UNAUTHORIZED, responseEvent.statusCode)

    }*/

    @Test
    fun testAddSendersEmailWithAuthToken() {

        val sendersEmailJson = buildSendersEmail("addSendersEmail.json")
        val requestEvent = requestSpecificationWithAuth(sendersEmailJson, token)
        val responseEvent = requestEvent.post(addSendersEmailUrl)
        assertEquals(HttpStatus.SC_OK, responseEvent.statusCode)

    }

    @Test
    fun testAddSendersEmailWithoutAuthToken() {

        val sendersEmailJson = buildSendersEmail("addSendersEmail.json")
        val requestEvent = requestSpecificationWithOutAuth(sendersEmailJson)
        val responseEvent = requestEvent.post(addSendersEmailUrl)
        assertEquals(HttpStatus.SC_UNAUTHORIZED, responseEvent.statusCode)

    }

    @Test
    fun testGetSendersEmailListWithAuthToken() {

        val requestEvent = requestSpecificationWithAuth(token)
        val responseEvent = requestEvent.get(sendersEmailListUrl)
        assertEquals(HttpStatus.SC_OK, responseEvent.statusCode)

    }

    @Test
    fun testGetSendersEmailListWithoutAuthToken() {

        val requestEvent = requestSpecificationWithOutAuth()
        val responseEvent = requestEvent.get(sendersEmailListUrl)
        assertEquals(HttpStatus.SC_UNAUTHORIZED, responseEvent.statusCode)

    }

    @Test
    fun testDeleteSendersEmailWithAuthToken() {

        val deleteSendersEmailJson = buildSendersEmail("deleteSendersEmail.json")
        val requestEvent = requestSpecificationWithAuth(deleteSendersEmailJson, token)
        val responseEvent = requestEvent.post(deleteSendersEmailUrl)
        assertEquals(HttpStatus.SC_OK, responseEvent.statusCode)

    }

    @Test
    fun testDeleteSendersEmailWithoutAuthToken() {

        val deleteSendersEmail = buildSendersEmail("deleteSendersEmail.json")
        val requestEvent = requestSpecificationWithOutAuth(deleteSendersEmail)
        val responseEvent = requestEvent.post(deleteSendersEmailUrl)
        assertEquals(HttpStatus.SC_UNAUTHORIZED, responseEvent.statusCode)

    }

   /* @Test
    fun testSaveAccountSettingsWithAuthToken() {
        //TODO Find the abnormality
        val accountSettingsJson = buildAccountSettings("accountSettings.json")
        val requestEvent = requestSpecificationWithAuth(accountSettingsJson, token)
        val responseEvent = requestEvent.post(saveAccountSettingsUrl)
        assertEquals(HttpStatus.SC_OK, responseEvent.statusCode)

    }*/

    @Test
    fun testSaveAccountSettingWithoutAuthToken() {
        val deleteSendersEmail = buildAccountSettings("accountSettings.json")
        val requestEvent = requestSpecificationWithOutAuth(deleteSendersEmail)
        val responseEvent = requestEvent.post(saveAccountSettingsUrl)
        assertEquals(HttpStatus.SC_UNAUTHORIZED, responseEvent.statusCode)
    }

    @Test
    fun testGetAccountSettingsWithAuthToken() {
        val requestEvent = requestSpecificationWithAuth(token)
        val responseEvent = requestEvent.get(accountSettingsUrl)
        assertEquals(HttpStatus.SC_OK, responseEvent.statusCode)
    }

    @Test
    fun testGetAccountSettingsWithoutAuthToken() {
        val requestEvent = requestSpecificationWithOutAuth()
        val responseEvent = requestEvent.get(accountSettingsUrl)
        assertEquals(HttpStatus.SC_UNAUTHORIZED, responseEvent.statusCode)
    }

    @Test
    fun testSaveUnsubscribeLinkWithAuthToken() {
        val unsubscribeLinkJson = buildUnsubscribeLink("unsubscribeLink.json")
        val requestEvent = requestSpecificationWithAuth(unsubscribeLinkJson, token)
        val responseEvent = requestEvent.post(saveUnsubscribeLinkUrl)
        assertEquals(HttpStatus.SC_OK, responseEvent.statusCode)
    }

    @Test
    fun testSaveUnsubscribeLinkWithoutAuthToken() {
        val unsubscribeLinkJson = buildUnsubscribeLink("unsubscribeLink.json")
        val requestEvent = requestSpecificationWithOutAuth(unsubscribeLinkJson)
        val responseEvent = requestEvent.post(saveUnsubscribeLinkUrl)
        assertEquals(HttpStatus.SC_UNAUTHORIZED, responseEvent.statusCode)
    }

    @Test
    fun testGetUnsubscribeLinkWithAuthToken() {
        val requestEvent = requestSpecificationWithAuth(token)
        val responseEvent = requestEvent.get(unsubscribeLinkUrl)
        assertEquals(HttpStatus.SC_OK, responseEvent.statusCode)
    }

    @Test
    fun testGetUnsubscribeLinkWithoutAuthToken() {
        val requestEvent = requestSpecificationWithOutAuth()
        val responseEvent = requestEvent.get(unsubscribeLinkUrl)
        assertEquals(HttpStatus.SC_UNAUTHORIZED, responseEvent.statusCode)
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