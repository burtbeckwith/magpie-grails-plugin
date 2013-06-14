package com.erasmos.grails.magpie_plugin

import org.apache.commons.lang.builder.ReflectionToStringBuilder
import org.apache.commons.lang.builder.ToStringStyle

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
        name(nullable: false, blank: false, unique: true)
        url(nullable: false)
        // TODO: Add custom constraint to ensure valid cron expression (Quartz)
        cronExpression(nullable: false, blank: false)
        enforcedContentTypeForRendering(nullable: true)
        active(nullable: false)
    }

    String toString(){
        return ReflectionToStringBuilder.toString(this,ToStringStyle.MULTI_LINE_STYLE)
    }
}
