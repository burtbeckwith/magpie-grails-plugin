package com.erasmos.grails.magpie_plugin

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@TestFor(MagpieService)
@Mock([Errand,Fetch])
@TestMixin([ValidationTestUtils,DomainTestUtils])
class MagpieServiceTests {

    def mockControlFetchService
    def mockControlJobService
    def mockControlEventService

    @Before
    void setUp(){

        mockControlFetchService = mockFor(FetchService)
        service.fetchService    = mockControlFetchService.createMock()

        mockControlJobService   = mockFor(JobService)
        service.jobService      = mockControlJobService.createMock()

        mockControlEventService = mockFor(EventService)
        service.eventService    = mockControlEventService.createMock()
    }

    @Test
    void createNewErrandWhenNameAlreadyTaken() {

        def name                            = 'Some Web Service'
        def url                             = DomainTestUtils.ValidURL
        def cronExpression                  = DomainTestUtils.ValidCronExpression
        def enforcedContentTypeForRendering = DomainTestUtils.ValidContentType

        generateErrand(name,url,cronExpression,enforcedContentTypeForRendering)
        assertNotNull(Errand.findByName(name))

        try  {

            service.createNewErrand(name,url,cronExpression,enforcedContentTypeForRendering)
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
     * Complete validation testing can be found in ErrandTests.
     */
    @Test
    void createNewErrandWhenSimpleValidationError() {

        def name                            = ' '
        def url                             = DomainTestUtils.ValidURL
        def cronExpression                  = DomainTestUtils.ValidCronExpression
        def enforcedContentTypeForRendering = DomainTestUtils.ValidContentType

        try  {

            service.createNewErrand(name,url,cronExpression,enforcedContentTypeForRendering)
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

        def name                            = 'Some Web Service'
        def url                             = DomainTestUtils.ValidURL
        def cronExpression                  = DomainTestUtils.ValidCronExpression
        def enforcedContentTypeForRendering = DomainTestUtils.ValidContentType

        assertNull(Errand.findByName(name))

        expectedAddJob(name,url,cronExpression)
        expectedOnNewErrand(name,url,cronExpression)

        def newErrand =  service.createNewErrand(name,url,cronExpression,enforcedContentTypeForRendering)

        assertNotNull(newErrand.id)
        assertNotNull(Errand.read(newErrand.id))
        assertEquals(name, newErrand.name)
        assertEquals(url, newErrand.url)
        assertEquals(cronExpression, newErrand.cronExpression)
        assertTrue(newErrand.active)
    }


    @Test
    void fetchErrand(){

        def errand = generateErrand(DomainTestUtils.ValidErrandName,DomainTestUtils.ValidURL,DomainTestUtils.ValidCronExpression,DomainTestUtils.ValidContentType)

        def returnedResponse = new FetchService.Response(httpStatusCode: 200, contentType: "text/html", contents: "Hello World".bytes)
        expectedFetch(errand.url,returnedResponse)

        expectedOnNewFetch(errand,returnedResponse.httpStatusCode,returnedResponse.contents)

        def fetch = service.fetch(errand)

        assertSame(errand,fetch.errand)
        assertEquals(returnedResponse.httpStatusCode,fetch.httpStatusCode)
        assertEquals(returnedResponse.contentType,fetch.contentType)
        Assert.assertArrayEquals(returnedResponse.contents,fetch.contents)


        assertSame(fetch, Fetch.findByErrand(errand))
        assertEquals(1, Fetch.count)
    }

    @Test
    void findErrandByNameWhenItDoesNotExist(){

        def name = 'My Errand'
        assertNull(Errand.findByName(name))

        def errand = service.findErrandByName(name)

        assertNull(errand)

    }

    @Test
    void findErrandByName(){

        def name = 'My Errand'
        def existingErrand = generateErrand(name)

        def errand = service.findErrandByName(name)

        assertSame(existingErrand,errand)

    }

    private void expectedOnNewFetch(final Errand expectedErrand, final int expectedHttpStatusCode, final byte[] expectedContents){

        mockControlEventService.demand.onNewFetch {
            Fetch _fetch ->
                assertSame(expectedErrand,_fetch.errand)
                assertEquals(expectedHttpStatusCode, _fetch.httpStatusCode)
                Assert.assertArrayEquals(expectedContents,_fetch.contents)
        }
    }



    @Test(expected = MagpieService.ErrandNotEligibleForFetch)
    void fetchErrandWhenNotEligible(){

        def errand = generateErrand(DomainTestUtils.ValidErrandName,DomainTestUtils.ValidURL,DomainTestUtils.ValidCronExpression,DomainTestUtils.ValidContentType,false)

        def returnedResponse = new FetchService.Response(httpStatusCode: 200, contents: "Hello World".bytes)
        expectedFetch(errand.url,returnedResponse)

        service.fetch(errand)
    }

    @Test
    void deactivateErrandWhenNotActive(){

        def errand = generateErrand(DomainTestUtils.ValidErrandName,DomainTestUtils.ValidURL,DomainTestUtils.ValidCronExpression,DomainTestUtils.ValidContentType,false)

        def verdict = service.deactivate(errand)

        assertTrue(verdict)
        assertFalse(errand.active)

    }

    @Test
    void deactivateErrandWhenWeFailToSaveIt(){

        def errand = generateErrand(DomainTestUtils.ValidErrandName,DomainTestUtils.ValidURL,DomainTestUtils.ValidCronExpression,DomainTestUtils.ValidContentType,true)

        def savedErrand = null
        expectValidateAndSave(errand,savedErrand)

        def verdict = service.deactivate(errand)

        assertFalse(verdict)
        assertTrue(errand.active)

    }


    @Test
    void deactivateErrand(){

        def errand = generateErrand(DomainTestUtils.ValidErrandName,DomainTestUtils.ValidURL,DomainTestUtils.ValidCronExpression,DomainTestUtils.ValidContentType,false)

        def verdict = service.activate(errand)

        assertTrue(verdict)
        assertTrue(errand.active)

    }


    @Test
    void activateErrandWhenActive(){

        def errand = generateErrand(DomainTestUtils.ValidErrandName,DomainTestUtils.ValidURL,DomainTestUtils.ValidCronExpression,DomainTestUtils.ValidContentType,true)

        def verdict = service.activate(errand)

        assertTrue(verdict)
        assertTrue(errand.active)

    }

    @Test
    void activateErrandWhenWeFailToSaveIt(){

        def errand = generateErrand(DomainTestUtils.ValidErrandName,DomainTestUtils.ValidURL,DomainTestUtils.ValidCronExpression,DomainTestUtils.ValidContentType,false)

        def savedErrand = null
        expectValidateAndSave(errand,savedErrand)

        def verdict = service.activate(errand)

        assertFalse(verdict)
        assertFalse(errand.active)

    }

    @Test
    void activateErrand(){

        def errand = generateErrand(DomainTestUtils.ValidErrandName,DomainTestUtils.ValidURL,DomainTestUtils.ValidCronExpression,DomainTestUtils.ValidContentType,false)

        def verdict = service.activate(errand)

        assertTrue(verdict)
        assertTrue(errand.active)

    }


    private void expectValidateAndSave(final Errand expectedErrand, final Errand returnedErrand){
        service.metaClass.validateAndSave {
            Errand _errand ->
                assertSame(expectedErrand,_errand)
                return returnedErrand
        }
    }

    private void expectedFetch(final URL expectedURL, final FetchService.Response returnedResponse){

        mockControlFetchService.demand.fetch {
            URL _url ->
                assertEquals(expectedURL,_url)
                return returnedResponse
        }
    }

    private void expectedOnNewErrand(final String expectedName, final URL expectedURL, final String expectedCronExpression) {

        mockControlEventService.demand.onNewErrand {
            Errand _errand ->
                assertEquals(expectedName,_errand.name)
                assertEquals(expectedURL,_errand.url)
                assertEquals(expectedCronExpression,_errand.cronExpression)
        }

    }


    private void expectedAddJob(final String expectedName, final URL expectedURL, final String expectedCronExpression) {

        mockControlJobService.demand.addJob {
            Errand _errand ->
                assertEquals(expectedName,_errand.name)
                assertEquals(expectedURL,_errand.url)
                assertEquals(expectedCronExpression,_errand.cronExpression)
        }
    }

}