package com.erasmos.grails.magpie_plugin



import grails.test.mixin.*
import org.junit.*

@TestFor(Fetch)
@TestMixin([ValidationTestUtils,DomainTestUtils])
@Mock(Errand)
class FetchTests {

    def errand

    @Before
    void setUp(){
        errand = generateValidErrand()
        assertNotNull(errand)
    }

    @Test
    void shouldBeInvalidWhenNameIsNull() {

        def proposedFetch = new Fetch(errand: null)
        proposedFetch.validate()

        assertTrue(proposedFetch.hasErrors())
        assertFieldError(proposedFetch,'errand','nullable')

    }

    @Test
    void shouldBeInvalidWhenHttpStatusCodeIsNull(){

        def proposedFetch = new Fetch(httpStatusCode: null)
        proposedFetch.validate()

        assertTrue(proposedFetch.hasErrors())
        assertFieldError(proposedFetch,'httpStatusCode','nullable')
    }

    @Test
    void shouldBeValidEvenWhenContentsIsNull(){

        def proposedFetch = new Fetch(errand:errand, httpStatusCode: 200, contents: null)
        proposedFetch.validate()

        assertFalse(proposedFetch.hasErrors())
    }


    @Test
    void shouldBeValid(){

        def proposedFetch = new Fetch(errand:errand, httpStatusCode: 200, contents: "Hello World".bytes)
        proposedFetch.validate()

        assertFalse(proposedFetch.hasErrors())
    }
}
