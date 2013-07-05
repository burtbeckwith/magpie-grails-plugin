package com.erasmos.grails.magpie_plugin

class MagpieService {

    static transactional = true

    FetchService    fetchService
    JobService      jobService
    EventService    eventService

    Errand createNewErrand(final String name,final URL url, final String cronExpression, final String enforcedContentTypeForRendering = null) throws InvalidProposedErrandException {

        def newErrand = validateAndSave(
                new Errand( name:name,
                            url:url,
                            cronExpression:cronExpression,
                            enforcedContentTypeForRendering:enforcedContentTypeForRendering,
                            active:true))

        jobService.addJob(newErrand)

        eventService.onNewErrand(newErrand)

        return newErrand

    }


    Fetch fetch(final Errand errand) throws ErrandNotEligibleForFetch{

        assert errand != null

        if(!errand.active) { throw new ErrandNotEligibleForFetch() }

        def newFetch = createFetch(errand,fetchService.fetch(errand.url))

        eventService.onNewFetch(newFetch)

        return newFetch

    }



    Errand findErrandByName(final String name){

        assert name != null

        return Errand.findByName(name)

    }

    /**
     *
     * @param errand
     * @return Whether it was successful or not.
     */
    boolean deactivate(final Errand errand){

        assert errand != null

        if(log.isDebugEnabled()) log.debug("Deactivating Errand:$errand ...")

        return setActive(errand,false)
    }

    /**
     *
     * @param errand
     * @return Whether it was successful or not.
     */
    boolean activate(final Errand errand){

        assert errand != null

        if(log.isDebugEnabled()) log.debug("Activating Errand:$errand ...")

        return setActive(errand,true)
    }

    boolean setActive(final Errand errand, final boolean active){

        assert errand != null


        if(errand.active==active){
            if(log.isDebugEnabled()) log.debug("... already set")
            return true
        }

        errand.active = active

        if(!validateAndSave(errand)){
            if(log.isErrorEnabled()) log.error("Failed to update Errand: $errand; errors were: ${errand.errors}")
            errand.active = !active
            return false
        }

        if(log.isDebugEnabled()) log.debug("... successful.")

        return true
    }



    private Fetch createFetch(final Errand errand, final FetchService.Response fetchServiceResponse) {

        assert fetchServiceResponse != null

        return validateAndSave(
                            new Fetch(
                                    errand: errand,
                                    httpStatusCode: fetchServiceResponse.httpStatusCode,
                                    contentType: fetchServiceResponse.contentType,
                                    contents: fetchServiceResponse.contents)
                        )
    }

    /**
     *
     * @param proposedErrand
     * @return
     */
    private Errand validateAndSave(final Errand proposedErrand){

        def savedErrand = proposedErrand.save()

        if(!savedErrand){
            if(log.isErrorEnabled()) {
                log.error("Invalid proposed Errand: $proposedErrand. Errors were: ${proposedErrand.errors}")
            }
            throw new InvalidProposedErrandException(proposedErrand)
        }

        assert savedErrand != null, "We guarantee to return an Errand (if there's no exception)"

        return savedErrand

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

    static class InvalidProposedErrandException extends Exception {
        Errand proposedErrand

        InvalidProposedErrandException(final Errand proposedErrand){
            this.proposedErrand = proposedErrand
        }
    }

    static class InvalidProposedFetchException extends Exception {
        Fetch proposedFetch

        InvalidProposedFetchException(final Errand proposedErrand){
            this.proposedFetch = proposedFetch
        }
    }


    static class ErrandNotEligibleForFetch extends Exception {

    }


}
