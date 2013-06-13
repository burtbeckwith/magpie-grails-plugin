package com.erasmos.grails.magpie_plugin



import grails.test.mixin.*
import org.junit.*
import org.springframework.context.ApplicationEvent

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(EventService)
class EventServiceTests {

    @Test
    void onNewFetch(){

        def fetch = new Fetch()

        expectPublishEventForNewFetch(fetch)

        service.onNewFetch(fetch)

    }



     @Test
    void onNewErrand(){

        def errand = new Errand()

        expectPublishEventForNewErrand(errand)

        service.onNewErrand(errand)
    }


    private void expectPublishEventForNewFetch(final Fetch expectedFetch){
        expectPublishEvent(EventService.NewFetchEvent,expectedFetch)
    }

    private void expectPublishEventForNewErrand(final Errand expectedErrand){
        expectPublishEvent(EventService.NewErrandEvent,expectedErrand)
    }

    /**
     * The publishEvent is injected by the spring-events plugin, so
     * we'll need to mock it here.
     */
    private void expectPublishEvent(final Class<ApplicationEvent> expectedEventClass, final Object expectedSource) {

        service.metaClass.publishEvent {
            Object _object ->
                assertEquals(expectedEventClass,_object.class)
                assertSame(expectedSource,((ApplicationEvent)_object).source)
        }
    }
}
