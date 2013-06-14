package com.erasmos.grails.magpie_plugin

/**
 *
 */
class DomainTestUtils {

    static final ValidErrandName        = 'Some Errand'
    static final ValidURL               = new URL('http://somewhere.org')
    static final ValidCronExpression    = '0 0 12 1/1 * ? *'
    static final ValidContentType       = 'application/json'


    /**
     *
     * @return
     */
    Errand generateErrand() {
        return generateErrand(ValidErrandName,ValidURL,ValidCronExpression,ValidContentType)
    }

    Errand generateErrand(final String name) {
        return generateErrand(name,ValidURL,ValidCronExpression,ValidContentType)
    }

    /**
     *
     * @param name
     * @param url
     * @param cronExpression
     * @param enforcedContentTypeForRendering
     * @param active
     * @return
     */
     Errand generateErrand(final String name, final URL url, final String cronExpression, final String enforcedContentTypeForRendering, final Boolean active = true){
        return new Errand(name:name,url:url,cronExpression: cronExpression, enforcedContentTypeForRendering:enforcedContentTypeForRendering, active:active).save(failOnError: true)
    }


    Fetch generateFetch(final Errand errand){
        return generateFetch(errand, new Date())
    }

    Fetch generateFetch(final Errand errand, final Date dateCreated){
        return new Fetch(errand: errand,dateCreated: dateCreated,httpStatusCode: 200,contents: "Hello World".bytes, contentType: ValidContentType).save(failOnError: true)
    }


}
