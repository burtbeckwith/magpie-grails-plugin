package com.erasmos.grails.magpie_plugin

import groovy.transform.Synchronized
import org.springframework.context.ApplicationListener

/**
 * TODO: Unit Test
 *
 * Currently only logs and counts the events.
 *
 */
class DefaultEventListenerService implements ApplicationListener<EventService.MagpieEvent>{

    private Map<Class<EventService.MagpieEvent>,Integer> eventCounts = [:]

    void onApplicationEvent(EventService.MagpieEvent event) {

        assert event != null

        logAndUpdateCount(event)
    }

    int getEventTypeCount(final Class<EventService.MagpieEvent> eventClass){
        return eventCounts[eventClass] ?: 0
    }

    private void logAndUpdateCount(final EventService.MagpieEvent magpieEvent){

        assert magpieEvent != null

        def currentCountForEventType =  updateCount(magpieEvent.class)

        if(log.isDebugEnabled()) log.debug("Received: $magpieEvent; there are now $currentCountForEventType such events.")

    }

    @Synchronized
    private int updateCount(final Class eventClass){

        if(!eventCounts.containsKey(eventClass)){
           eventCounts[eventClass] = 0
        }

        eventCounts[eventClass] = eventCounts[eventClass] + 1

        return eventCounts[eventClass]

    }
}
