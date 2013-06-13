package com.erasmos.grails.magpie_plugin

/**
 *
 */
class DomainTestUtils {

    static final ValidErrandName      = 'Some Errand'

    static final ValidURL             = new URL('http://somewhere.org')
    static final ValidCronExpression  = '0 0 12 1/1 * ? *'

    /**
     *
     * @param name
     * @return
     */
    Errand generateErrand() {
        return generateErrand(ValidErrandName,ValidURL,ValidCronExpression)
    }

    /**
     *
     * @param name
     * @param url
     * @param cronExpression
     * @return
     */
     Errand generateErrand(final String name, final URL url, final String cronExpression, final Boolean active = true){

        def newErrand = new Errand(name:name,url:url,cronExpression: cronExpression, active:active).save(true)
        assert newErrand != null
        return newErrand
    }



    Errand generateValidErrand(){
        return generateErrand(ValidErrandName,ValidURL,ValidCronExpression)
    }

}
