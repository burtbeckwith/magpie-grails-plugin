package com.erasmos.grails.magpie_plugin

class MagpieService {

    FetchService fetchService
    EventService eventService

    Errand createNewErrand(final String name,final URL url, final String cronExpression) throws InvalidProposedErrandException {

        assert name             != null
        assert url              != null
        assert cronExpression   != null

        def newErrand = validateAndSave(new Errand(name:name,url:url,cronExpression:cronExpression,active:true))

        eventService.onNewErrand(newErrand)

        return newErrand

    }

    Fetch fetch(final Errand errand){

        assert errand != null

        if(!errand.active) { throw new ErrandNotEligibleForFetch() }

        def newFetch = createFetch(errand,fetchService.fetch(errand.url))

        eventService.onNewFetch(newFetch)

        return newFetch

    }

    private Fetch createFetch(final Errand errand, final FetchService.Response fetchServiceResponse) {

        assert fetchServiceResponse != null

        return validateAndSave(
                            new Fetch(
                                    errand: errand,
                                    httpStatusCode: fetchServiceResponse.httpStatusCode,
                                    contents: fetchServiceResponse.contents)
                        )
    }

    /**
     *
     * @param proposedErrand
     * @return
     */
    private Errand validateAndSave(final Errand proposedErrand){

        def newErrand = proposedErrand.save()

        if(!newErrand){
            if(log.isErrorEnabled()) {
                log.error("Invalid proposed Errand: $proposedErrand. Errors were: ${proposedErrand.errors}")
            }
            throw new InvalidProposedErrandException(proposedErrand)
        }

        assert newErrand != null, "We guarantee to return an Errand (if there's no exception)"

        return newErrand

    }

    /**
     * I wouldn't expect this to ever happen, as we only create the Fetch internally
     *
     * @param proposedFetch
     * @return
     */
    private Fetch validateAndSave(final Fetch proposedFetch){

        def newFetch = proposedFetch.save()

        if(!newFetch){
            if(log.isErrorEnabled()) {
                log.error("Invalid proposed Fetch: $proposedFetch. Errors were: ${proposedFetch.errors}")
            }
            throw new InvalidProposedFetchException()
        }

        return newFetch

    }

    public static class InvalidProposedErrandException extends Exception {
        Errand proposedErrand

        InvalidProposedErrandException(final Errand proposedErrand){
            this.proposedErrand = proposedErrand
        }
    }

    public static class InvalidProposedFetchException extends Exception {
        Fetch proposedFetch

        InvalidProposedFetchException(final Errand proposedErrand){
            this.proposedFetch = proposedFetch
        }
    }


    public static class ErrandNotEligibleForFetch extends Exception {

    }


}
