package com.erasmos.grails.magpie_plugin

class Fetch {

    static final MaxSizeForContentsInMB = 2

    Errand  errand
    Date    dateCreated // Automatically provided
    Integer httpStatusCode // TODO: Have a Fetch Status
    byte[]  contents

    static constraints = {
        errand(nullable: false)
        dateCreated(nullable: true)  // Automatically provided by framework
        httpStatusCode(nullable: false)
        contents(nullable: true) // Could be legitimately absent
    }

    static mapping = {
        contents maxSize: 1024 * 1024 * MaxSizeForContentsInMB
    }
}
