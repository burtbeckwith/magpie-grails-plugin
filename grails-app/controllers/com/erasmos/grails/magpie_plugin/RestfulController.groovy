package com.erasmos.grails.magpie_plugin

import grails.converters.JSON
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import org.springframework.http.HttpStatus

class RestfulController {

    static final RestfulUrlBase = 'restfulMagpie'

    LinkGenerator grailsLinkGenerator

    def index() { }

    def showAllErrands() {
        render(Errand.all as JSON)
    }

    def showErrand(){

        def errand = Errand.read(params.id)
        if(!errand) {
            render(status: HttpStatus.NOT_FOUND)
            return
        }


        render(errand as JSON)
    }

    def showFetchesForErrand(){

        def errand = Errand.read(params.id)
        if(!errand) {
            render(status: HttpStatus.NOT_FOUND)
            return
        }

        render(Fetch.findAllByErrand(errand) as JSON)

    }

    void registerJSONMarshallers(){

        int marshallerPriority = 0

        [Errand,Fetch].each {  Class _class ->
            JSON.registerObjectMarshaller(_class,marshallerPriority) {return asMapForJSON(it)}
        }
    }

    private Map asMapForJSON(final Errand errand) {
        assert errand != null

        return [
                name:               errand.name,
                url:                errand.url,
                cronExpression:     errand.cronExpression,
                active:             errand.active,
                numberOfFetches:    Fetch.countByErrand(errand),
                links: [
                        fetches:    generateLinkToFetchesForErrand(errand.id),
                        allErrands: generateLinkToFetchesForAllErrands()
                        ]

        ]
    }


    private Map asMapForJSON(final Fetch fetch) {
        assert fetch != null

        return [
            errandId:       fetch.errand.id,
            errandName:     fetch.errand.name,
            date:           fetch.dateCreated,
            httpStatusCode: fetch.httpStatusCode,
            contentSize:    fetch.contentsSize,
            links: [
                errand:    generateLinkToToErrand(fetch.errand.id)
                    ]
        ]
    }


    private String generateLinkToFetchesForErrand(final Long errandId) {
        assert errandId != null
        return "$serverBaseURL/$RestfulUrlBase/errands/${errandId}/fetches"
    }

    private String generateLinkToFetchesForAllErrands() {
        return "$serverBaseURL/$RestfulUrlBase/errands"
    }


    private String generateLinkToToErrand(final Long errandId) {
        assert errandId != null
        return "$serverBaseURL/$RestfulUrlBase/errands/${errandId}"
    }

    /**
     *
     * @return
     */
    private String getServerBaseURL(){
        return grailsLinkGenerator.serverBaseURL
    }
}
