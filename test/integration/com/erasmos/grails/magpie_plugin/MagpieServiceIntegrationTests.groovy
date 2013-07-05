package com.erasmos.grails.magpie_plugin

import org.junit.Test

/**
 * An integration test, as I'm actually fetching the Errand's content.
 */
class MagpieServiceIntegrationTests extends GroovyTestCase {

    private static final ValidCronExpression    = '0 0 12 1/1 * ? *'
    private static final ValidContentType       = 'application/json'

    MagpieService   magpieService
    JobService      jobService

    /**
     * Needed to ensure that the events are being received.
     */
    DefaultEventListenerService defaultEventListenerService

    /**
     *
     */
    @Test
    void createNewErrand() {

        def name                                = 'Currency (GBP -> USD)'
        def url                                 = generateCurrencyRelatedUrl('GBP','USD')
        def cronExpression                      = ValidCronExpression
        def enforcedContentTypeForRendering     = ValidContentType

        def numberOfNewErrandEventsBefore = getInitialNumberOfSuchEvents(EventService.NewErrandEvent)

        assertFalse(jobService.doesJobExist(name))

        def errand = magpieService.createNewErrand(name,url,cronExpression,enforcedContentTypeForRendering)

        assertNotNull(errand)
        assertSame(errand,Errand.read(errand.id))
        assertEquals(name,errand.name)
        assertEquals(url,errand.url)
        assertEquals(cronExpression,errand.cronExpression)
        assertEquals(enforcedContentTypeForRendering,errand.enforcedContentTypeForRendering)
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
        assertEquals('application/octet-stream',fetch.contentType)
        def contentsAString = fetch.contentsAsString
        assertTrue(contentsAString.contains("GBPCAD=X"))

        def numberOfNewFetchEventsAfter = getNumberOfSuchEventsAfterwards(EventService.NewFetchEvent)
        assertEquals(numberOfNewFetchEventsBefore+1,numberOfNewFetchEventsAfter)
    }

    /**
     *
     * @param eventClass
     * @return
     */
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

    /**
     *
     */
    @Test
    void fetchErrandForCurrencyRateCADAndGBP(){

        def errand = generateCurrencyRelatedErrand('CAD','GBP')

        def numberOfNewFetchEventsBefore = getInitialNumberOfSuchEvents(EventService.NewFetchEvent)

        def fetch = magpieService.fetch(errand)

        assertEquals(200,fetch.httpStatusCode)
        assertEquals('application/octet-stream',fetch.contentType)
        def contentsAString = fetch.contentsAsString
        assertTrue(contentsAString.contains("CADGBP=X"))

        def numberOfNewFetchEventsAfter = getNumberOfSuchEventsAfterwards(EventService.NewFetchEvent)
        assertEquals(numberOfNewFetchEventsBefore+1,numberOfNewFetchEventsAfter)
    }

    /**
     *
     */
    @Test
    void fetchErrandForURLForUnknownSite(){

        def errand = generateErrand(new URL('http://upload.finance.yahoo.com/'))

        def numberOfNewFetchEventsBefore = getInitialNumberOfSuchEvents(EventService.NewFetchEvent)

        def fetch = magpieService.fetch(errand)

        assertEquals(503,fetch.httpStatusCode)

        def numberOfNewFetchEventsAfter = getNumberOfSuchEventsAfterwards(EventService.NewFetchEvent)
        assertEquals(numberOfNewFetchEventsBefore+1,numberOfNewFetchEventsAfter)
    }

    /**
     *
     * @param fromCurrencySymbol
     * @param toCurrencySymbol
     * @param active
     * @return
     */
    private Errand generateCurrencyRelatedErrand(final String fromCurrencySymbol, final String toCurrencySymbol, final boolean active = true) {

        return new Errand(  name:"Currency ($fromCurrencySymbol -> $toCurrencySymbol)",
                            url:  generateCurrencyRelatedUrl(fromCurrencySymbol,toCurrencySymbol),
                            cronExpression: ValidCronExpression,
                            active: active
                            ).save(failOnError: true)
    }

    /**
     *
     * @param url
     * @param active
     * @return
     */
    private Errand generateErrand(final URL url, final boolean active = true) {

        return new Errand(
                name: url.toExternalForm(),
                url:  url,
                cronExpression: ValidCronExpression,
                active: active
        ).save(failOnError: true)
    }

    /**
     * Example: curl "http://download.finance.yahoo.com/d/quotes.cvs?s=GBPCAD=X&f=sl1d1t1ba&e=.csv"
     *
     * @param fromCurrencySymbol
     * @param toCurrencySymbol
     * @return
     */
    private URL generateCurrencyRelatedUrl(final String fromCurrencySymbol, final String toCurrencySymbol){
        return new URL("http://download.finance.yahoo.com/d/quotes.cvs?s=${fromCurrencySymbol}${toCurrencySymbol}=X&f=sl1d1t1ba&e=.csv")
    }
}
