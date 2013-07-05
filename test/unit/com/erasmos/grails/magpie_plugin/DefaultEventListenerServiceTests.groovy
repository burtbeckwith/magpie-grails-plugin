package com.erasmos.grails.magpie_plugin

import grails.test.mixin.TestFor
import org.junit.Test

/**
 *
 */
@TestFor(DefaultEventListenerService)
class DefaultEventListenerServiceTests {

    /**
     *
     */
    @Test
    void onNewFetchEvent() {
        onEvent(new EventService.NewFetchEvent(new Fetch()))
    }

    /**
     *
     */
    @Test
    void onNewErrandEvent() {
        onEvent(new EventService.NewErrandEvent(new Errand()))
    }

    /**
     *
     * @param expectedMagpieEvent
     */
    private void onEvent(final EventService.MagpieEvent expectedMagpieEvent){

        def beforeCount = service.getEventTypeCount(expectedMagpieEvent.class)

        service.onApplicationEvent(expectedMagpieEvent)

        def afterCount = service.getEventTypeCount(expectedMagpieEvent.class)

        assertEquals(beforeCount+1,afterCount)
    }
}
