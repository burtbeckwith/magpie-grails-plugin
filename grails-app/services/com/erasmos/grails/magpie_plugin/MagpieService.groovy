package com.erasmos.grails.magpie_plugin

class MagpieService {

    public Errand createNewErrand(final String name,final URL url, final String cronExpression) throws InvalidProposedErrandException {

        assert name             != null
        assert url              != null
        assert cronExpression   != null

        return validateAndSave(new Errand(name:name,url:url,cronExpression:cronExpression))

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

    public static class InvalidProposedErrandException extends Exception {
        Errand proposedErrand

        InvalidProposedErrandException(final Errand proposedErrand){
            this.proposedErrand = proposedErrand
        }
    }

}
