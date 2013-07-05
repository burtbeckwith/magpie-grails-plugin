package com.erasmos.grails.magpie_plugin

import groovy.transform.Synchronized
import org.springframework.context.ApplicationListener

/**
 * Currently only logs and counts the events; more for demo than anything but perhaps
 * could as a comforting sign that all is working well.
 */
class DefaultEventListenerService implements ApplicationListener<EventService.MagpieEvent>{

    private Map<Class<EventService.MagpieEvent>,Integer> eventCounts = [:]

    /**
     *
     * @param event
     */
    void onApplicationEvent(EventService.MagpieEvent event) {
        assert event != null

        logAndUpdateCount(event)
    }

    /**
     *
     * @param eventClass
     * @return
     */
    int getEventTypeCount(final Class<EventService.MagpieEvent> eventClass){
        return eventCounts[eventClass] ?: 0
    }

    /**
     *
     * @param magpieEvent
     */
    private void logAndUpdateCount(final EventService.MagpieEvent magpieEvent){
        assert magpieEvent != null

        def currentCountForEventType =  updateCount(magpieEvent.class)

        if(log.isDebugEnabled()) log.debug("Received: $magpieEvent; there are now $currentCountForEventType such events.")
    }

    /**
     *
     * @param eventClass
     * @return
     */
    @Synchronized
    private int updateCount(final Class eventClass){
        assert eventClass != null

        if(!eventCounts.containsKey(eventClass)){
           eventCounts[eventClass] = 0
        }

        eventCounts[eventClass] = eventCounts[eventClass] + 1

        return eventCounts[eventClass]
    }
}
