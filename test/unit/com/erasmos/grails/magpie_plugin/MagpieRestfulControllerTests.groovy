package com.erasmos.grails.magpie_plugin



import grails.test.mixin.*
import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONObject
import org.junit.*

/**
 * TODO: Add tests
 */
@TestFor(MagpieRestfulController)
@TestMixin(DomainTestUtils)
@Mock([Errand,Fetch])
class MagpieRestfulControllerTests {

    static String ServerBaseURL     = 'http://localhost:8080/magpie'
    static String ContentTypeJSON   = 'application/json;charset=UTF-8'

    def mockControlMagpieService

    @Before
    void setUp() {

        mockControlMagpieService    = mockFor(MagpieService)
        controller.magpieService    = mockControlMagpieService.createMock()

        controller.registerJSONMarshallers()

        expectGetServerBaseURL(ServerBaseURL)
    }

    @Test
    void index() {

        controller.index()

        assertEquals(200,response.status)
        assertEquals(ContentTypeJSON,response.contentType)
        assertNotNull(response.json)
        assertTrue(response.json instanceof JSONObject)
    }

    @Test
    void showAllErrandsWhenNoneExist() {

        controller.showAllErrands()

        assertEquals(200,response.status)
        assertEquals(ContentTypeJSON,response.contentType)
    }


    @Test
    void showAllErrandsWhenSomeExist() {

        def errandOne = generateErrand('Get Bananas')
        def errandTwo = generateErrand('Get Apples')

        controller.showAllErrands()

        assertEquals(200,response.status)
        assertEquals(ContentTypeJSON,response.contentType)
        assertNotNull(response.json)
        assertTrue(response.json instanceof JSONArray)

        def jsonArray = (JSONArray)response.json
        assertEquals(2,jsonArray.size())
        assertEquals('Should be sorted by Errand.name',errandTwo.id,jsonArray.get(0)['id'])
        assertEquals(errandOne.id,jsonArray.get(1)['id'])

        jsonArray.each {assertIsErrand(it)}
    }

    @Test
    void showErrandWhenUnknown(){

        def errandId = 8888
        assertFalse(Errand.exists(errandId))

        params.id = errandId as String

        controller.showErrand()

        assertEquals(404,response.status)
        assertEquals("Unknown Errand: 8888", response.text)


    }

    @Test
    void showErrand(){

        def errand = generateErrand()

        params.id = errand.id as String

        controller.showErrand()

        assertEquals(200,response.status)
        assertEquals(ContentTypeJSON,response.contentType)
        assertNotNull(response.json)
        assertTrue(response.json instanceof JSONObject)
        assertIsErrand(response.json)

    }

    @Test
    void showAllFetchesWhenNoneExist() {

        controller.showAllFetches()

        assertEquals(200,response.status)
        assertEquals(ContentTypeJSON,response.contentType)
    }


    @Test
    void showAllFetchesWhereSomeExist() {

        def errand = generateErrand()

        def fetchFromYesterday  = generateFetch(errand, new Date() - 1)
        def fetchFromToday      = generateFetch(errand, new Date())

        controller.showAllFetches()

        assertEquals(200,response.status)
        assertEquals(ContentTypeJSON,response.contentType)
        assertNotNull(response.json)
        assertTrue(response.json instanceof JSONArray)

        def jsonArray = (JSONArray)response.json
        assertEquals(2,jsonArray.size())
        assertEquals('Should be sorted by Fetch.dateCreated (most recent first)',fetchFromToday.id,jsonArray.get(0)['id'])
        assertEquals(fetchFromYesterday.id,jsonArray.get(1)['id'])
    }

    @Test
    void showFetchesForErrandForUnknownErrand() {

        def errandId = 8888
        assertFalse(Errand.exists(errandId))

        params.id = errandId as String

        controller.showFetchesForErrand()

        assertEquals(404,response.status)
        assertEquals("Unknown Errand: 8888", response.text)
    }


    @Test
    void showFetchesForAnErrandWithoutFetches() {

        def errand = generateErrand()
        assertEquals(0,Fetch.countByErrand(errand))

        params.id = errand.id as String

        controller.showFetchesForErrand()

        assertEquals(200,response.status)
        assertEquals(ContentTypeJSON,response.contentType)
        assertNotNull(response.json)
        assertTrue(response.json instanceof JSONArray)

        def jsonArray = (JSONArray)response.json
        assertTrue(jsonArray.empty)
    }

     @Test
    void showFetchesForAnErrandWithFetches() {

        def errand = generateErrand()

        def fetchFromYesterday  = generateFetch(errand, new Date() - 1)
        def fetchFromToday      = generateFetch(errand, new Date())

        params.id = errand.id as String

        controller.showFetchesForErrand()

        assertEquals(200,response.status)
        assertEquals(ContentTypeJSON,response.contentType)
        assertNotNull(response.json)
        assertTrue(response.json instanceof JSONArray)

        def jsonArray = (JSONArray)response.json
        assertEquals(2,jsonArray.size())
        assertEquals('Should be sorted by Fetch.dateCreated (most recent first)',fetchFromToday.id,jsonArray.get(0)['id'])
        assertEquals(fetchFromYesterday.id,jsonArray.get(1)['id'])
    }

    @Test
    void showContentsForFetchWhenUnknown() {

        def fetchId = 8888
        assertFalse(Fetch.exists(fetchId))

        params.id = fetchId as String

        controller.showContentsForFetch()

        assertEquals(404,response.status)
        assertEquals("Unknown Fetch: 8888", response.text)
    }

    @Test
    void errandAsMapForJSON() {

        def errand = generateErrand()

        generateFetch(errand, new Date() - 1)
        generateFetch(errand, new Date())

        def errandAsMapForJSON = controller.asMapForJSON(errand)

        assertEquals(errand.id,errandAsMapForJSON['id'])
        assertEquals(errand.name,errandAsMapForJSON['name'])
        assertEquals(errand.url,errandAsMapForJSON['url'])
        assertEquals(errand.cronExpression,errandAsMapForJSON['cronExpression'])
        assertEquals(errand.enforcedContentTypeForRendering,errandAsMapForJSON['enforcedContentTypeForRendering'])
        assertTrue(errandAsMapForJSON['active'])
        assertEquals(2,errandAsMapForJSON['numberOfFetches'])

        def links = errandAsMapForJSON['links'] as Map
        assertEquals(2,links.size())
        assertEquals("http://localhost:8080/magpie/restfulMagpie/errands/${errand.id}/fetches".toString(),links['fetches'])
        assertEquals('http://localhost:8080/magpie/restfulMagpie/errands',links['allErrands'])

    }

    @Test
    void fetchAsMapForJSON() {

        def errand = generateErrand()

        def fetch = generateFetch(errand)

        def fetchAsMapForJSON = controller.asMapForJSON(fetch)

        assertEquals(fetch.id,fetchAsMapForJSON['id'])
        assertEquals(errand.id,fetchAsMapForJSON['errandId'])
        assertEquals(errand.name,fetchAsMapForJSON['errandName'])
        assertEquals(errand.enforcedContentTypeForRendering,fetchAsMapForJSON['errandEnforcedContentTypeForRendering'])
        assertEquals(fetch.dateCreated.toString(),fetchAsMapForJSON['date'])
        assertEquals(fetch.httpStatusCode,fetchAsMapForJSON['httpStatusCode'])
        assertEquals(fetch.contentType,fetchAsMapForJSON['contentType'])
        assertEquals(fetch.contentsSize,fetchAsMapForJSON['contentSize'])

        def links = fetchAsMapForJSON['links'] as Map
        assertEquals(2,links.size())
        assertEquals("http://localhost:8080/magpie/restfulMagpie/errands/${errand.id}".toString(),links['errand'])
        assertEquals("http://localhost:8080/magpie/restfulMagpie/fetches/${fetch.id}/contents".toString(),links['contents'])

    }

    @Test
    void generateIndexMapForJSON() {

        def errandOne = generateErrand('One')
        def errandTwo = generateErrand('Two')

        generateFetch(errandOne)
        generateFetch(errandOne)
        generateFetch(errandTwo)

        def indexMapForJSON = controller.generateIndexMapForJSON()

        assertEquals(2,indexMapForJSON['numberOfErrands'])
        assertEquals(3,indexMapForJSON['numberOfFetches'])

        def links = indexMapForJSON['links'] as Map
        assertEquals(2,links.size())
        assertEquals("http://localhost:8080/magpie/restfulMagpie/errands".toString(),links['allErrands'])
        assertEquals("http://localhost:8080/magpie/restfulMagpie/fetches".toString(),links['allFetches'])

    }

    @Test
    void createNewErrandWhenInvalidProposedErrandException(){


        def name                                = DomainTestUtils.ValidErrandName
        def url                                 = DomainTestUtils.ValidURL.toExternalForm()
        def cronExpression                      = DomainTestUtils.ValidCronExpression
        def enforcedContentTypeForRendering     = DomainTestUtils.ValidContentType

        params.name                            = name
        params.url                             = url
        params.cronExpression                  = cronExpression
        params.enforcedContentTypeForRendering = enforcedContentTypeForRendering

        def proposedErrand = new Errand()
        proposedErrand.validate() // To generate some fieldErrors
        def thrown = new MagpieService.InvalidProposedErrandException(proposedErrand)
        expectCreateNewErrand(name,url,cronExpression,enforcedContentTypeForRendering,thrown)

        controller.createErrand()

        assertEquals(400,response.status)
        assertEquals(ContentTypeJSON,response.contentType)
        assertNotNull(response.json)
        assertTrue(response.json instanceof JSONObject)
        assertIsErrors(response.json)
    }

    @Test
    void validationErrorsAsMapForJSON() {

        def errand = new Errand()
        errand.validate()

        def errorsAsMap = controller.asMapForJSON(errand.errors)

        assertEquals(1,errorsAsMap.size())
        def fieldErrors = errorsAsMap['fieldErrors']
        assertEquals(errand.errors.fieldErrorCount,fieldErrors.size())
        fieldErrors.each { Map _fieldErrorAsMap ->
            assertEquals(3,_fieldErrorAsMap.size())
            assertTrue(_fieldErrorAsMap.containsKey('field'))
            assertTrue(_fieldErrorAsMap.containsKey('rejectedValue'))
            assertTrue(_fieldErrorAsMap.containsKey('code'))
        }
    }

    private void assertIsErrors(final JSONObject jsonObject) {

        assertEquals(1,jsonObject.size())
        assertTrue(jsonObject.has('fieldErrors'))
    }

    @Test
    void createNewErrand(){

        def name                                = DomainTestUtils.ValidErrandName
        def url                                 = DomainTestUtils.ValidURL.toExternalForm()
        def cronExpression                      = DomainTestUtils.ValidCronExpression
        def enforcedContentTypeForRendering     = DomainTestUtils.ValidContentType

        params.name                            = name
        params.url                             = url
        params.cronExpression                  = cronExpression
        params.enforcedContentTypeForRendering = enforcedContentTypeForRendering

        def returnedNewErrand = generateErrand()
        expectCreateNewErrand(name,url,cronExpression,enforcedContentTypeForRendering,returnedNewErrand)

        controller.createErrand()

        assertEquals(200,response.status)
        assertEquals(ContentTypeJSON,response.contentType)
        assertNotNull(response.json)
        assertTrue(response.json instanceof JSONObject)
        assertIsErrand(response.json)

    }

    private void assertIsErrand(final JSONObject jsonObject) {

        assertNotNull(jsonObject.get('id'))
        assertNotNull(jsonObject.get('name'))
        assertNotNull(jsonObject.get('active'))
        assertNotNull(jsonObject.get('numberOfFetches'))
        assertNotNull(jsonObject.get('cronExpression'))
        assertNotNull(jsonObject.get('url'))
        assertNotNull(jsonObject.get('enforcedContentTypeForRendering'))

    }

    private void expectCreateNewErrand(final String expectedName, final String expectedUrl, final String expectedCronExpression, final String expectedEnforcedContentTypeForRendering, final Errand returnedErrand) {

        mockControlMagpieService.demand.createNewErrand {
            String _name,
            String _url,
            String _cronExpression,
            String _enforcedContentTypeForRendering ->

                assertEquals(expectedName,_name)
                assertEquals(expectedUrl,_url)
                assertEquals(expectedCronExpression,_cronExpression)
                assertEquals(expectedEnforcedContentTypeForRendering,_enforcedContentTypeForRendering)

                return returnedErrand

        }
    }


    private void expectCreateNewErrand(final String expectedName, final String expectedUrl, final String expectedCronExpression, final String expectedEnforcedContentTypeForRendering, final Throwable thrown) {

        mockControlMagpieService.demand.createNewErrand {
            String _name,
            String _url,
            String _cronExpression,
            String _enforcedContentTypeForRendering ->

                assertEquals(expectedName,_name)
                assertEquals(expectedUrl,_url)
                assertEquals(expectedCronExpression,_cronExpression)
                assertEquals(expectedEnforcedContentTypeForRendering,_enforcedContentTypeForRendering)

                throw thrown

        }
    }


    /**
     * I wasn't able to mock the LinkGenerator; no matter, Grails
     * automatically injects one, even for unit tests.
     *
     * @param returnedServerBaseURL
     */
    private void expectGetServerBaseURL(final String returnedServerBaseURL){
        controller.grailsLinkGenerator.metaClass.getServerBaseURL {->
            return returnedServerBaseURL
        }
    }
}
