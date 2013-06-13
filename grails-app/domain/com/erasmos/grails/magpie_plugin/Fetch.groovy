package com.erasmos.grails.magpie_plugin

import org.apache.commons.lang.builder.ReflectionToStringBuilder
import org.apache.commons.lang.builder.ToStringStyle

import java.nio.charset.Charset

class Fetch {

    static final MaxSizeForContentsInMB = 2

    Errand  errand
    Date    dateCreated // Automatically provided
    Integer httpStatusCode // TODO: Have a Fetch Status
    byte[]  contents

    static transients = ['contentsAsString']

    static constraints = {
        errand(nullable: false)
        dateCreated(nullable: true)  // Automatically provided by framework
        httpStatusCode(nullable: false)
        contents(nullable: true) // Could be legitimately absent
    }

    static mapping = {
        contents (maxSize: 1024 * 1024 * MaxSizeForContentsInMB, sqlType: 'blob')
    }

    /**
     * Might have to introduce a char-set at some point.
     * @return
     */
    String getContentsAsString(){

        if(contents==null) return null

        return new String(contents)
    }

    String toString(){
        return ReflectionToStringBuilder.toString(this,ToStringStyle.MULTI_LINE_STYLE)
    }
}
