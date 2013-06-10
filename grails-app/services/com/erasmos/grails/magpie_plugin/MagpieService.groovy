package com.erasmos.grails.magpie_plugin

class MagpieService {

    FetchService fetchService

    Errand createNewErrand(final String name,final URL url, final String cronExpression) throws InvalidProposedErrandException {

        assert name             != null
        assert url              != null
        assert cronExpression   != null

        return validateAndSave(new Errand(name:name,url:url,cronExpression:cronExpression,active:true))

    }

    Fetch fetch(final Errand errand){

        assert errand != null

        if(!errand.active) { throw new ErrandNotEligibleForFetch() }

        return createFetch(errand,fetchService.fetch(errand.url))

    }

    private Fetch createFetch(final Errand errand, final FetchService.Response fetchServiceResponse) {

        assert fetchServiceResponse != null

        return validateAndSave(new Fetch(
                                    errand: errand,
                                    httpStatusCode: fetchServiceResponse.httpStatusCode,
                                    contents: fetchServiceResponse.contents)
        )
    }

    private Errand validateAndSave(final Errand proposedErrand){

        def newErrand = proposedErrand.save()

        if(!newErrand){
            if(log.isErrorEnabled()) {
                log.error("Invalid proposed Errand: $proposedErrand. Errors were: ${proposedErrand.errors}")
            }
            throw new InvalidProposedErrandException(proposedErrand)
        }

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
            throw new InvalidProposedFetchException(proposedFetch)
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
