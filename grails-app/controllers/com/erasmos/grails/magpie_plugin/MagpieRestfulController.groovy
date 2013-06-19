package com.erasmos.grails.magpie_plugin

import grails.converters.JSON
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import org.springframework.http.HttpStatus

class MagpieRestfulController {

    static final RestfulUrlBase = 'restfulMagpie'

    LinkGenerator grailsLinkGenerator

    def index() {
        render(generateIndexMapForJSON() as JSON)
    }

    /**
     * TODO: DB style sorting (that would work with unit tests)
     * @return
     */
    def showAllErrands() {

        def allErrandsSortedByName = Errand.all.sort {a,b -> a.name <=> b.name}

        render(allErrandsSortedByName as JSON)
    }

    def showErrand(){

        def errand = figureRequestedErrand()
        if(!errand) return

        render(errand as JSON)
    }

    /**
     * TODO: DB style sorting (that would work with unit tests)
     * @return
     */
    def showAllFetches() {

        def allFetchesSortedByReverseDate = Fetch.all.sort {a,b -> b.dateCreated <=> a.dateCreated}

        render(allFetchesSortedByReverseDate as JSON)
    }

    def showFetchesForErrand(){

        def errand = figureRequestedErrand()
        if(!errand) return

        def allFetchesForErrandSortedByReverseDate = Fetch.findAllByErrand(errand,[sort:'dateCreated',order:'desc'])

        render(allFetchesForErrandSortedByReverseDate as JSON)
    }


    def showContentsForFetch() {

        def fetch = figureRequestedFetch()
        if(!fetch) return

        renderContents(fetch)
    }


    private Errand figureRequestedErrand() {

        def errand = params.id ? Errand.read(params.id) : null
        if(!errand) {
            render(status: HttpStatus.NOT_FOUND,text: "Unknown Errand: ${params.id}")
        }

        return errand
    }

    private Fetch figureRequestedFetch() {

        def fetch = params.id ? Errand.read(params.id) : null
        if(!fetch) {
            render(status: HttpStatus.NOT_FOUND,text: "Unknown Fetch: ${params.id}")
        }

        return fetch
    }

    /**
     * TODO: Seems to be a known bug with Grails (when I used the second method
     * it tries to locate the view)
     *
     * @param fetch
     */
    private void renderContents(final Fetch fetch) {

        def contentTypeForRendering = fetch.contentTypeForRendering

        if(log.isDebugEnabled()) log.debug("We'll render the Fetch's content as: $contentTypeForRendering")


        if(contentTypeForRendering == 'text/html') {
            render(contentType:contentTypeForRendering, text:fetch.contentsAsString)
        }
        else {
            response.contentType = contentTypeForRendering
            response.outputStream << fetch.contents
        }
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
                id:                                 errand.id,
                name:                               errand.name,
                url:                                errand.url,
                cronExpression:                     errand.cronExpression,
                enforcedContentTypeForRendering:    errand.enforcedContentTypeForRendering,
                active:                             errand.active,
                numberOfFetches:                    Fetch.countByErrand(errand),
                links: [
                        fetches:    generateLinkToFetchesForErrand(errand.id),
                        allErrands: generateLinkToAllErrands()
                        ]

        ]
    }


    private Map asMapForJSON(final Fetch fetch) {
        assert fetch != null

        return [
            id:                                     fetch.id,
            errandId:                               fetch.errand.id,
            errandName:                             fetch.errand.name,
            errandEnforcedContentTypeForRendering:  fetch.errand.enforcedContentTypeForRendering,
            date:                                   fetch.dateCreated.toString(),
            httpStatusCode:                         fetch.httpStatusCode,
            contentType:                            fetch.contentType,
            contentSize:                            fetch.contentsSize,
            links: [
                errand:     generateLinkToToErrand(fetch.errand.id),
                contents:   generateLinkToFetchContents(fetch.id)
                    ]
        ]
    }


    private Map generateIndexMapForJSON(){

        return [
                numberOfErrands:    Errand.count(),
                numberOfFetches:    Fetch.count(),
                links: [
                    allErrands:     generateLinkToAllErrands(),
                    allFetches:     generateLinkToAllFetches()
                ]
        ]
    }

    private String generateLinkToFetchesForErrand(final Long errandId) {
        assert errandId != null
        return "$serverBaseURL/$RestfulUrlBase/errands/${errandId}/fetches"
    }

    private String generateLinkToAllErrands() {
        return "$serverBaseURL/$RestfulUrlBase/errands"
    }

    private String generateLinkToAllFetches() {
        return "$serverBaseURL/$RestfulUrlBase/fetches"
    }

    private String generateLinkToToErrand(final Long errandId) {
        assert errandId != null
        return "$serverBaseURL/$RestfulUrlBase/errands/${errandId}"
    }

    private String generateLinkToFetchContents(final Long fetchId) {
        assert fetchId != null
        return "$serverBaseURL/$RestfulUrlBase/fetches/${fetchId}/contents"
    }

    /**
     *
     * @return
     */
    private String getServerBaseURL(){
        return grailsLinkGenerator.serverBaseURL
    }


}