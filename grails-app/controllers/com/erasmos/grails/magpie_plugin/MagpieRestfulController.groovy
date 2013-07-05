package com.erasmos.grails.magpie_plugin

import grails.converters.JSON
import grails.validation.ValidationErrors
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import org.springframework.http.HttpStatus
import org.springframework.validation.FieldError

/**
 *
 */
class MagpieRestfulController {

    private static final RestfulUrlBase = 'restfulMagpie'

    MagpieService magpieService
    LinkGenerator grailsLinkGenerator

    /**
     *
     * @return
     */
    def index() {
        render(generateIndexMapForJSON() as JSON)
    }

    /**
     *
     */
    def showAllErrands() {

        def allErrandsSortedByName = Errand.all.sort {a,b -> a.name <=> b.name}

        render(allErrandsSortedByName as JSON)
    }

    /**
     *
     */
    def showErrand(){

        def errand = figureRequestedErrand()
        if(!errand) return

        render(errand as JSON)
    }

    /**
     *
     */
    def showFetch(){

        def fetch = figureRequestedFetch()
        if(!fetch) return

        render(fetch as JSON)
    }

    /**
     *
     */
    def showAllFetches() {

        def allFetchesSortedByReverseDate = Fetch.all.sort {a,b -> b.dateCreated <=> a.dateCreated}

        render(allFetchesSortedByReverseDate as JSON)
    }

    /**
     *
     */
    def showFetchesForErrand(){

        def errand = figureRequestedErrand()
        if(!errand) return

        if(log.isDebugEnabled()) log.debug("Request to show all Fetches for Errand #: ${errand.id} ...")

        def allFetchesForErrandSortedByReverseDate = Fetch.findAllByErrand(errand,[sort:'dateCreated',order:'desc'])

        if(log.isDebugEnabled()) log.debug("... there are ${allFetchesForErrandSortedByReverseDate.size()} of them.")

        render(allFetchesForErrandSortedByReverseDate as JSON)
    }

    /**
     *
     */
    def showContentsForFetch() {

        def fetch = figureRequestedFetch()
        if(!fetch) return

        renderContents(fetch)
    }

    /**
     *
     */
    def createErrand() {

        try {

            def newErrand = magpieService.createNewErrand(params.name,toUrl(params.url),params.cronExpression,params.enforcedContentTypeForRendering)
            addLocationHeader(generateLinkToErrand(newErrand.id))
            render(status: HttpStatus.CREATED.value())

        }
        catch (MagpieService.InvalidProposedErrandException ex) {
            response.status = HttpStatus.BAD_REQUEST.value()
            render(ex.proposedErrand as JSON)
        }
    }

    /**
     *
     */
    def fetchErrand() {

        def errand = figureRequestedErrand()
        if(!errand) return

        try {
            def newFetch =  magpieService.fetch(errand)
            addLocationHeader(generateLinkToFetch(newFetch.id))
            render(status: HttpStatus.CREATED.value())
        }
        catch (MagpieService.ErrandNotEligibleForFetch ex) {
            render(status: HttpStatus.UNAUTHORIZED.value(),text:"Errand #${errand.id} is not eligible for fetching.")
        }
    }

    /**
     *
     * @param rawUrl
     * @return
     */
    private URL toUrl(final String rawUrl){
        if(!rawUrl) return null

        try {
            return new URL(rawUrl)
        }
        catch(MalformedURLException ex){
            if(log.isErrorEnabled()){
                log.error("Invalid URL: $rawUrl");
            }
            return null
        }
    }

    /**
     *
     * @param url
     */
    private void addLocationHeader(final String url){
        response.addHeader('Location',url)
    }

    /**
     *
     * @return
     */
    private Errand figureRequestedErrand() {

        def errand = params.id ? Errand.read(params.id) : null
        if(!errand) {
            render(status: HttpStatus.NOT_FOUND,text: "Unknown Errand: ${params.id}")
        }

        return errand
    }

    /**
     *
     * @return
     */
    private Fetch figureRequestedFetch() {

        def fetch = params.id ? Fetch.read(params.id) : null
        if(!fetch) {
            render(status: HttpStatus.NOT_FOUND,text: "Unknown Fetch: ${params.id}")
        }

        return fetch
    }

    /**
     * Seems to be a known bug with Grails (when I used the second method
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

    /**
     *                                    z
     */
    void registerJSONMarshallers(){

        int marshallerPriority = 0

        [Errand, Fetch, ValidationErrors].each {  Class _class ->
            JSON.registerObjectMarshaller(_class,marshallerPriority) {return asMapForJSON(it)}
        }
    }

    /**
     *
     * @param errand
     * @return
     */
    private Map asMapForJSON(final Errand errand) {
        assert errand != null

        if(errand.hasErrors()) {

            def fieldErrorsAsMaps = errand.errors.fieldErrors.collect {asMapForJSON(it)}

            return [
                    name:                               errand.name,
                    url:                                errand.url,
                    cronExpression:                     errand.cronExpression,
                    enforcedContentTypeForRendering:    errand.enforcedContentTypeForRendering,
                    fieldErrors:                        fieldErrorsAsMaps
            ]
        }

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

    /**
     *
     * @param fetch
     * @return
     */
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
                errand:     generateLinkToErrand(fetch.errand.id),
                contents:   generateLinkToFetchContents(fetch.id)
                    ]
        ]
    }

    /**
     *
     * @return
     */
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

    /**
     *
     * @param fieldError
     * @return
     */
    private Map asMapForJSON(final FieldError fieldError) {
        assert fieldError != null

        return [
                field: fieldError.field,
                rejectedValue: fieldError.rejectedValue,
                code: fieldError.code
        ]
    }

    /**
     *
     * @param errandId
     * @return
     */
    private String generateLinkToFetchesForErrand(final Long errandId) {
        assert errandId != null
        return "$serverBaseURL/$RestfulUrlBase/errands/${errandId}/fetches"
    }

    /**
     *
     * @return
     */
    private String generateLinkToAllErrands() {
        return "$serverBaseURL/$RestfulUrlBase/errands"
    }

    /**
     *
     * @return
     */
    private String generateLinkToAllFetches() {
        return "$serverBaseURL/$RestfulUrlBase/fetches"
    }

    /**
     *
     * @param errandId
     * @return
     */
    private String generateLinkToErrand(final Long errandId) {
        assert errandId != null
        return "$serverBaseURL/$RestfulUrlBase/errands/${errandId}"
    }

    /**
     *
     * @param fetchId
     * @return
     */
    private String generateLinkToFetch(final Long fetchId) {
        assert fetchId != null
        return "$serverBaseURL/$RestfulUrlBase/fetches/${fetchId}"
    }

    /**
     *
     * @param fetchId
     * @return
     */
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
