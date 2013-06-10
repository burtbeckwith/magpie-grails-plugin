package com.erasmos.grails.magpie_plugin

class Errand {

    String  name
    URL     url
    String  cronExpression

    static constraints = {
        name(nullable: false, blank: false, unique: true)
        url(nullable: false)
        // TODO: Add custom constraint to ensure valid cron expression (Quartz)
        cronExpression(nullable: false, blank: false)
    }
}
