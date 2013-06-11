package com.erasmos.grails.magpie_plugin

import org.junit.*



class MagpieServiceIntegrationTests extends GroovyTestCase {


    static final ValidCronExpression  = '0 0 12 1/1 * ? *'
    static final UseLocalProxy = true

    MagpieService magpieService


    @Before
    void setUp() {
        magpieService = new MagpieService()
        magpieService.fetchService = new FetchService()
        magpieService.fetchService.useLocalProxy = UseLocalProxy
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

    /**
     *
     */
    @Test
    void fetchErrandForCurrencyRateGBPAndCAD(){

        def errand = generateCurrencyRelatedErrand('GBP','CAD')

        def fetch = magpieService.fetch(errand)

        assertEquals(200,fetch.httpStatusCode)
        def contentsAString = fetch.contentsAsString
        assertTrue(contentsAString.contains("GBPCAD=X"))

    }


    @Test
    void fetchErrandForCurrencyRateCADAndGBP(){

        def errand = generateCurrencyRelatedErrand('CAD','GBP')

        def fetch = magpieService.fetch(errand)

        assertEquals(200,fetch.httpStatusCode)
        def contentsAString = fetch.contentsAsString
        assertTrue(contentsAString.contains("CADGBP=X"))
    }

    @Test
    void fetchErrandForURLForUnknownSite(){

        def errand = generateErrand(new URL('http://upload.finance.yahoo.com/'))

        def fetch = magpieService.fetch(errand)

        assertEquals(503,fetch.httpStatusCode)
    }


    private Errand generateCurrencyRelatedErrand(final String fromCurrencySymbol, final String toCurrencySymbol, final boolean active = true) {

        return new Errand(  name:"Currency ($fromCurrencySymbol -> $toCurrencySymbol)",
                            url:  generateCurrencyRelatedUrl(fromCurrencySymbol,toCurrencySymbol),
                            cronExpression: ValidCronExpression,
                            active: active
                            ).save(failOnError: true)
    }

    private Errand generateErrand(final URL url, final boolean active = true) {

        return new Errand(
                name: url.toExternalForm(),
                url:  url,
                cronExpression: ValidCronExpression,
                active: active
        ).save(failOnError: true)
    }

    /**
     * curl "http://download.finance.yahoo.com/d/quotes.cvs?s=GBPCAD=X&f=sl1d1t1ba&e=.csv"
     *
     * @param fromCurrencySymbol
     * @param toCurrencySymbol
     * @return
     */
    private URL generateCurrencyRelatedUrl(final String fromCurrencySymbol, final String toCurrencySymbol){
        return new URL("http://download.finance.yahoo.com/d/quotes.cvs?s=${fromCurrencySymbol}${toCurrencySymbol}=X&f=sl1d1t1ba&e=.csv")
    }
}
