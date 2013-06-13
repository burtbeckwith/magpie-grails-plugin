package com.erasmos.grails.magpie_plugin

import org.junit.Before
import org.junit.Test

class MagpieServiceIntegrationTests extends GroovyTestCase {



    static final ValidCronExpression  = '0 0 12 1/1 * ? *'
    static final UseLocalProxy = false

    MagpieService   magpieService
    JobService      jobService

    /**
     * Needed to ensure that the events are being received.
     */
    DefaultEventListenerService defaultEventListenerService


    @Before
    void setUp() {
        magpieService.fetchService.useLocalProxy = UseLocalProxy
    }

    @Test
    void createNewErrand() {

        def name                = 'Currency (GBP -> USD)'
        def url                 = generateCurrencyRelatedUrl('GBP','USD')
        def cronExpression      = ValidCronExpression

        def numberOfNewErrandEventsBefore = getInitialNumberOfSuchEvents(EventService.NewErrandEvent)

        assertFalse(jobService.doesJobExist(name))

        def errand = magpieService.createNewErrand(name,url,cronExpression)

        assertNotNull(errand)
        assertSame(errand,Errand.read(errand.id))
        assertEquals(name,errand.name)
        assertEquals(url,errand.url)
        assertEquals(cronExpression,errand.cronExpression)
        assertTrue(errand.active)

        def numberOfNewErrandEventsAfter = getNumberOfSuchEventsAfterwards(EventService.NewErrandEvent)

        assertEquals(numberOfNewErrandEventsBefore+1,numberOfNewErrandEventsAfter)


        assertTrue(jobService.doesJobExist(name))

    }

    /**
     *
     */
    @Test
    void fetchErrandForCurrencyRateGBPAndCAD(){

        def errand = generateCurrencyRelatedErrand('GBP','CAD')

        def numberOfNewFetchEventsBefore = getInitialNumberOfSuchEvents(EventService.NewFetchEvent)

        def fetch = magpieService.fetch(errand)

        assertEquals(200,fetch.httpStatusCode)
        def contentsAString = fetch.contentsAsString
        assertTrue(contentsAString.contains("GBPCAD=X"))

        def numberOfNewFetchEventsAfter = getNumberOfSuchEventsAfterwards(EventService.NewFetchEvent)
        assertEquals(numberOfNewFetchEventsBefore+1,numberOfNewFetchEventsAfter)
    }

    private int getInitialNumberOfSuchEvents(final Class<EventService.MagpieEvent> eventClass) {
        return defaultEventListenerService.getEventTypeCount(eventClass)
    }

    /**
     * Taking a brief nap here as the event broadcast is async; yes, probably a better way
     * of doing this.
     *
     * @param eventClass
     * @return
     */
    private int getNumberOfSuchEventsAfterwards(final Class<EventService.MagpieEvent> eventClass){
        sleep(100)
        return defaultEventListenerService.getEventTypeCount(eventClass)
    }

    @Test
    void fetchErrandForCurrencyRateCADAndGBP(){

        def errand = generateCurrencyRelatedErrand('CAD','GBP')

        def numberOfNewFetchEventsBefore = getInitialNumberOfSuchEvents(EventService.NewFetchEvent)

        def fetch = magpieService.fetch(errand)

        assertEquals(200,fetch.httpStatusCode)
        def contentsAString = fetch.contentsAsString
        assertTrue(contentsAString.contains("CADGBP=X"))

        def numberOfNewFetchEventsAfter = getNumberOfSuchEventsAfterwards(EventService.NewFetchEvent)
        assertEquals(numberOfNewFetchEventsBefore+1,numberOfNewFetchEventsAfter)
    }

    @Test
    void fetchErrandForURLForUnknownSite(){

        def errand = generateErrand(new URL('http://upload.finance.yahoo.com/'))

        def numberOfNewFetchEventsBefore = getInitialNumberOfSuchEvents(EventService.NewFetchEvent)

        def fetch = magpieService.fetch(errand)

        assertEquals(503,fetch.httpStatusCode)

        def numberOfNewFetchEventsAfter = getNumberOfSuchEventsAfterwards(EventService.NewFetchEvent)
        assertEquals(numberOfNewFetchEventsBefore+1,numberOfNewFetchEventsAfter)
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
