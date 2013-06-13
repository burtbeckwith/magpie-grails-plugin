package com.erasmos.grails.magpie_plugin

import org.apache.commons.lang.builder.ReflectionToStringBuilder
import org.apache.commons.lang.builder.ToStringStyle

class Errand {

    String  name
    URL     url
    String  cronExpression
    Boolean active

    static constraints = {
        name(nullable: false, blank: false, unique: true)
        url(nullable: false)
        // TODO: Add custom constraint to ensure valid cron expression (Quartz)
        cronExpression(nullable: false, blank: false)
        active(nullable: false)
    }

    String toString(){
        return ReflectionToStringBuilder.toString(this,ToStringStyle.MULTI_LINE_STYLE)
    }
}
