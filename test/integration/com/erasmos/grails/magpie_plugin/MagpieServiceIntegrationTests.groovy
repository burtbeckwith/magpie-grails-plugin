package com.erasmos.grails.magpie_plugin

import org.junit.*



class MagpieServiceIntegrationTests extends GroovyTestCase {


    static final ValidCronExpression  = '0 0 12 1/1 * ? *'

    MagpieService magpieService


    @Before
    void setUp() {
        magpieService = new MagpieService()
        magpieService.fetchService = new FetchService()
    }

    @After
    void tearDown() {
        // Tear down logic here
    }

    @Test
    void createNewErrand() {

        def name                = 'Currency (GBP -> USD)'
        def url                 = generateCurrencyRelatedUrl('GBP','USD')
        def cronExpression      = ValidCronExpression

        def errand = magpieService.createNewErrand(name,url,cronExpression)

        assertNotNull(errand)
        assertSame(errand,Errand.read(errand.id))
        assertEquals(name,errand.name)
        assertEquals(url,errand.url)
        assertEquals(cronExpression,errand.cronExpression)
        assertTrue(errand.active)

    }

    // TODO: fetchErrand

    private URL generateCurrencyRelatedUrl(final String fromCurrencySymbol, final String toCurrencySymbol){
        return new URL("http://www.google.com/ig/calculator?hl=en&q=1${fromCurrencySymbol}=?${toCurrencySymbol}")
    }
}
