package com.erasmos.grails.magpie_plugin

import org.apache.commons.lang.builder.ReflectionToStringBuilder
import org.apache.commons.lang.builder.ToStringStyle
import org.springframework.context.ApplicationEvent

/**
 * The publishEvent method is injected by the spring-events plugin,
 * so is only available when deployed.
 */
class EventService {

    /**
     *
     * @param fetch
     */
    void onNewFetch(final Fetch fetch) {

        assert fetch != null

        publish(new NewFetchEvent(fetch))

    }

    /**
     *
     * @param errand
     */
    void onNewErrand(final Errand errand) {

        assert errand != null

        publish(new NewErrandEvent(errand))

    }


    private void publish(final MagpieEvent newEvent){

        assert newEvent != null

        publishEvent(newEvent)
    }

    public interface MagpieEvent{}

    static class NewFetchEvent extends ApplicationEvent implements MagpieEvent{

        NewFetchEvent(final Fetch newFetch){
            super(newFetch)
        }

        String toString(){
            return "New Fetch: $source"
        }
    }

    static class NewErrandEvent extends ApplicationEvent implements MagpieEvent{

        NewErrandEvent(final Errand newErrandEvent){
            super(newErrandEvent)
        }

        String toString(){
            return "New Errand: $source"
        }
    }
}
