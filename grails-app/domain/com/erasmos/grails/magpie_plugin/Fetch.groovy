package com.erasmos.grails.magpie_plugin

import org.apache.commons.lang.builder.ReflectionToStringBuilder
import org.apache.commons.lang.builder.ToStringStyle

import java.nio.charset.Charset

class Fetch {

    static final MaxSizeForContentsInMB = 2

    Errand  errand
    Date    dateCreated // Automatically provided
    Integer httpStatusCode // TODO: Have a Fetch Status
    String  contentType
    byte[]  contents

    static transients = ['contentsAsString','contentsSize','contentTypeForRendering']

    static constraints = {
        errand(nullable: false)
        dateCreated(nullable: true)  // Automatically provided by framework
        httpStatusCode(nullable: false) // TODO: Probably should be nullable, as when can't even connect.
        contentType(nullable: true) // Could be legitimately absent
        contents(nullable: true) // Could be legitimately absent
    }

    static mapping = {
        table 'errand_fetch' // Fetch is a reserved word, at least in Mysql.
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

    int getContentsSize(){
        return contents ? contents.size() : 0
    }

    String getContentTypeForRendering(){
        return (errand.enforcedContentTypeForRendering) ?: contentType
    }

    String toString(){
        def stringBuilder = new ReflectionToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
        stringBuilder.setExcludeFieldNames(['contents'] as String[])
        return stringBuilder.toString()
    }
}
