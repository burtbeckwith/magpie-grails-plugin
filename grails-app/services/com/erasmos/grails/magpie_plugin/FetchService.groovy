package com.erasmos.grails.magpie_plugin

class FetchService {

    /**
     * TODO: Use RestBuilder ...
     * @param url
     * @return
     */
    FetchService.Response fetch(final URL url){
        return new Response()
    }

    public static class Response {
        Integer httpStatusCode
        byte[] contents
    }
}
