package com.erasmos.grails.magpie_plugin



import grails.test.mixin.*
import org.junit.*

@TestFor(Errand)
@TestMixin([ValidationTestUtils,DomainTestUtils])
class ErrandTests {


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

        def existingErrand = generateErrand(DomainTestUtils.ValidErrandName,DomainTestUtils.ValidURL,DomainTestUtils.ValidCronExpression)

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


    @Test
    void shouldBeValid() {

        def proposedErrand = new Errand(
                name:DomainTestUtils.ValidErrandName,
                url: DomainTestUtils.ValidURL,
                cronExpression: DomainTestUtils.ValidCronExpression,
                active: true)

        proposedErrand.validate()

        assertFalse(proposedErrand.hasErrors())

    }

}
