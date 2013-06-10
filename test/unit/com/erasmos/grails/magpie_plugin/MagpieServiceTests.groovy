package com.erasmos.grails.magpie_plugin



import grails.test.mixin.*
import org.junit.*

@TestFor(MagpieService)
@Mock(Errand)
class MagpieServiceTests {

    static ValidURL = new URL('http://somewhere.org')
    static ValidCronExpression = '0 0 12 1/1 * ? *'


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


    }

    private void assertFieldError(final Errand proposedErrand, final String fieldName, final String expectedCode) {
        assertEquals(expectedCode,proposedErrand.errors.getFieldError(fieldName).code)
    }

    private Errand generateErrand(final String name, final URL url, final String cronExpression){
        return new Errand(name:name,url:url,cronExpression: cronExpression).save(true)
    }

}