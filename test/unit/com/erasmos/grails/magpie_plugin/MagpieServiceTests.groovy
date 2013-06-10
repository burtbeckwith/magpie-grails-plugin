package com.erasmos.grails.magpie_plugin



import grails.test.mixin.*
import org.junit.*

@TestFor(MagpieService)
@Mock([Errand,Fetch])
@TestMixin([ValidationTestUtils,DomainTestUtils])
class MagpieServiceTests {

    static final ValidName            = 'Some Errand'
    static final ValidURL             = new URL('http://somewhere.org')
    static final ValidCronExpression  = '0 0 12 1/1 * ? *'

    def mockControlFetchService

    @Before
    void setUp(){

        mockControlFetchService = mockFor(FetchService)
        service.fetchService    = mockControlFetchService.createMock()

    }

    @Test
    void createNewErrandWhenNameAlreadyTaken() {

        def name            = 'Some Web Service'
        def url             = ValidURL
        def cronExpression  = ValidCronExpression

        generateErrand(name,url,cronExpression)
        assertNotNull(Errand.findByName(name))

        try  {

            service.createNewErrand(name,url,cronExpression)
            fail('Expected an InvalidProposedErrandException')
        }
        catch(MagpieService.InvalidProposedErrandException ex){

            def proposedErrand = ex.proposedErrand

            assertNotNull(proposedErrand)
            assertTrue(proposedErrand.hasErrors())
            assertEquals(1, proposedErrand.errors.errorCount)
            assertFieldError(proposedErrand,'name','unique')
            assertEquals(name,proposedErrand.name)
            assertEquals(url,proposedErrand.url)
            assertEquals(cronExpression,proposedErrand.cronExpression)

        }

    }

    /**
     * TODO: Complete validation testing will be found in
     * ErrandTests.
     */
    @Test
    void createNewErrandWhenSimpleValidationError() {

        def name            = ' '
        def url             = ValidURL
        def cronExpression  = ValidCronExpression

        try  {

            service.createNewErrand(name,url,cronExpression)
            fail('Expected an InvalidProposedErrandException')
        }
        catch(MagpieService.InvalidProposedErrandException ex){

            def proposedErrand = ex.proposedErrand

            assertNotNull(proposedErrand)
            assertTrue(proposedErrand.hasErrors())
            assertEquals(1, proposedErrand.errors.errorCount)
            assertFieldError(proposedErrand,'name','blank')
            assertEquals(name,proposedErrand.name)
            assertEquals(url,proposedErrand.url)
            assertEquals(cronExpression,proposedErrand.cronExpression)

        }


    }

    @Test
    void createNewErrandSuccessfully() {

        def name            = 'Some Web Service'
        def url             = ValidURL
        def cronExpression  = ValidCronExpression

        assertNull(Errand.findByName(name))

        def newErrand =  service.createNewErrand(name,url,cronExpression)

        assertNotNull(newErrand.id)
        assertNotNull(Errand.read(newErrand.id))
        assertEquals(name, newErrand.name)
        assertEquals(url, newErrand.url)
        assertEquals(cronExpression, newErrand.cronExpression)
        assertTrue(newErrand.active)


    }

    @Test
    void fetchErrand(){

        def errand = generateErrand(ValidName,ValidURL,ValidCronExpression)

        def returnedResponse = new FetchService.Response(httpStatusCode: 200, contents: "Hello World".bytes)
        expectedFetch(errand.url,returnedResponse)

        def fetch = service.fetch(errand)

        assertSame(errand,fetch.errand)
        assertEquals(returnedResponse.httpStatusCode,fetch.httpStatusCode)
        Assert.assertArrayEquals(returnedResponse.contents,fetch.contents)


        assertSame(fetch, Fetch.findByErrand(errand))
        assertEquals(1, Fetch.count)
    }


    @Test(expected = MagpieService.ErrandNotEligibleForFetch)
    void fetchErrandWhenNotEligible(){

        def errand = generateErrand(ValidName,ValidURL,ValidCronExpression,false)

        def returnedResponse = new FetchService.Response(httpStatusCode: 200, contents: "Hello World".bytes)
        expectedFetch(errand.url,returnedResponse)

        service.fetch(errand)
    }
\
    private void expectedFetch(final URL expectedURL, final FetchService.Response returnedResponse){

        mockControlFetchService.demand.fetch {
            URL _url ->
                assertEquals(expectedURL,_url)
                return returnedResponse
        }
    }

}