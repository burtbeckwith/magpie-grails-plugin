package com.erasmos.grails.magpie_plugin

import org.apache.commons.lang.builder.ReflectionToStringBuilder
import org.apache.commons.lang.builder.ToStringStyle

/**
 * Defines the act of fetching the content of the Errand's URL.
 */
class Fetch {

    private static final MaxSizeForContentsInMB = 2

    Errand  errand
    Date    dateCreated // Automatically provided by Grails
    Integer httpStatusCode
    String  contentType
    byte[]  contents

    static transients = ['contentsAsString','contentsSize','contentTypeForRendering']

    static constraints = {
        errand(nullable: false)
        dateCreated(nullable: true)  // Automatically provided by framework
        httpStatusCode(nullable: false)
        contentType(nullable: true) // Could be legitimately absent
        contents(nullable: true) // Could be legitimately absent
    }

    static mapping = {
        table 'errand_fetch' // Fetch is a reserved word, at least in Mysql.
        contents (maxSize: 1024 * 1024 * MaxSizeForContentsInMB, sqlType: 'blob')
    }

    /**
     * @return
     */
    String getContentsAsString(){
        if(contents==null) return null
        return new String(contents)
    }

    /**
     *
     * @return
     */
    int getContentsSize(){
        return contents ? contents.size() : 0
    }

    /**
     *
     * @return
     */
    String getContentTypeForRendering(){
        return (errand.enforcedContentTypeForRendering) ?: contentType
    }

    /**
     *
     * @return
     */
    String toString(){
        def stringBuilder = new ReflectionToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
        stringBuilder.setExcludeFieldNames(['contents'] as String[])
        return stringBuilder.toString()
    }
}
