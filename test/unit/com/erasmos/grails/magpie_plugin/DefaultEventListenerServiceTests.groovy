package com.erasmos.grails.magpie_plugin



import grails.test.mixin.*
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(DefaultEventListenerService)
class DefaultEventListenerServiceTests {

    @Test
    void onNewFetchEvent() {
        onEvent(new EventService.NewFetchEvent(new Fetch()))
    }

    @Test
    void onNewErrandEvent() {
        onEvent(new EventService.NewErrandEvent(new Errand()))
    }

    private void onEvent(final EventService.MagpieEvent expectedMagpieEvent){

        def beforeCount = service.getEventTypeCount(expectedMagpieEvent.class)

        service.onApplicationEvent(expectedMagpieEvent)

        def afterCount = service.getEventTypeCount(expectedMagpieEvent.class)

        assertEquals(beforeCount+1,afterCount)

    }
}
