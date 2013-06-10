package com.erasmos.grails.magpie_plugin



import grails.test.mixin.*
import org.junit.*

@TestFor(Errand)
class ErrandTests {

    static ValidName            = 'Some Errand'
    static ValidURL             = new URL('http://somewhere.org')
    static ValidCronExpression  = '0 0 12 1/1 * ? *'

    @Test
    void shouldBeInvalidWhenNameIsNull() {

        def proposedErrand = new Errand(name:null)
        proposedErrand.validate()

        assertTrue(proposedErrand.hasErrors())
        assertFieldError(proposedErrand,'name','nullable')

    }

    @Test
    void shouldBeInvalidWhenNameIsBlank() {

        def proposedErrand = new Errand(name:'  ')
        proposedErrand.validate()

        assertTrue(proposedErrand.hasErrors())
        assertFieldError(proposedErrand,'name','blank')

    }

    @Test
    void shouldBeInvalidWhenNameHasBeenTaken() {

        def existingErrand = generateErrand(ValidName,ValidURL,ValidCronExpression)

        def proposedErrand = new Errand(name:existingErrand.name)
        proposedErrand.validate()

        assertTrue(proposedErrand.hasErrors())
        assertFieldError(proposedErrand,'name','unique')

    }

    @Test
    void shouldBeInvalidWhenUrlIsNull() {

        def proposedErrand = new Errand(url:null)
        proposedErrand.validate()

        assertTrue(proposedErrand.hasErrors())
        assertFieldError(proposedErrand,'url','nullable')

    }


    @Test
    void shouldBeInvalidWhenCronExpressionIsNull() {

        def proposedErrand = new Errand(cronExpression: null)
        proposedErrand.validate()

        assertTrue(proposedErrand.hasErrors())
        assertFieldError(proposedErrand,'cronExpression','nullable')

    }



    @Test
    void shouldBeInvalidWhenCronExpressionIsBlank() {

        def proposedErrand = new Errand(cronExpression: '  ')
        proposedErrand.validate()

        assertTrue(proposedErrand.hasErrors())
        assertFieldError(proposedErrand,'cronExpression','blank')

    }


    /**
     * TODO: De-dupe
     * @param proposedErrand
     * @param fieldName
     * @param expectedCode
     */
    private void assertFieldError(final Errand proposedErrand, final String fieldName, final String expectedCode) {
        assertEquals(expectedCode,proposedErrand.errors.getFieldError(fieldName).code)
    }

    /**
     * TODO: De-dupe
     * @param name
     * @param url
     * @param cronExpression
     * @return
     */
    private Errand generateErrand(final String name, final URL url, final String cronExpression){
        return new Errand(name:name,url:url,cronExpression: cronExpression).save(true)
    }

}
