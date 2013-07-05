package com.erasmos.grails.magpie_plugin

import org.apache.commons.lang.builder.ReflectionToStringBuilder
import org.apache.commons.lang.builder.ToStringStyle

/**
 * An Errand defines the scheduled task of getting the contents for a specified URL.
 */
class Errand {

    String  name
    URL     url
    String  cronExpression
    /**
     * While the actual content type is preserved in the Fetches, sometimes
     * we want to force the rendering of the content differently; for example,
     * if there's a site that sends back JSON as 'text/html', we can forcibly
     * render is as 'application/json'
     */
    String enforcedContentTypeForRendering
    Boolean active

    static constraints = {
        name(blank: false, unique: true)
        url()
        cronExpression(blank: false)
        enforcedContentTypeForRendering(nullable: true)
        active()
    }

    String toString(){
        return ReflectionToStringBuilder.toString(this,ToStringStyle.MULTI_LINE_STYLE)
    }
}
