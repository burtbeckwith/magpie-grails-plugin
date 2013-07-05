package com.erasmos.grails.magpie_plugin

import org.apache.commons.lang.builder.ReflectionToStringBuilder
import org.apache.commons.lang.builder.ToStringStyle
import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.util.EntityUtils

class FetchService {

    static transactional = false

    /**
     *
     * @param url
     * @return
     */
    FetchService.Response fetch(final URL url){
        assert url != null

        def urlAsString = url.toExternalForm()

        if(log.isDebugEnabled()) log.debug("Attempting to fetch from url: ($urlAsString) ...")

        def httpClient = generateHttpClient()
        def httpGet = new HttpGet(urlAsString);

        def response = null
        try {

           def httpResponse = httpClient.execute(httpGet)

           def contentType     = extractContentType(httpResponse)
           def contents        = extractContents(httpResponse)

           response =  new Response(
                        httpStatusCode: httpResponse.statusLine.statusCode,
                        contentType: contentType,
                        contents: contents)

           if(log.isDebugEnabled()) log.debug("Response: $response")
        }
        catch (UnknownHostException ex){

           if(log.isErrorEnabled()) log.error("Unknown host for url:$urlAsString")

           response =  generateResponseForUnknownHost()
        }
        finally {
           httpGet.releaseConnection()
        }

        return response
    }

    /**
     *
     * @return
     */
    private Response generateResponseForUnknownHost(){
        new Response(httpStatusCode: 503,
                contentType: null,
                contents: null)
    }

    /**
     *
     * @param httpResponse
     * @return
     */
    private String extractContentType(final HttpResponse httpResponse){
        assert httpResponse != null
        return httpResponse.getHeaders('Content-type').first().value
    }

    /**
     *
     * @param httpResponse
     * @return
     */
    private byte[] extractContents(final HttpResponse httpResponse) {
        assert httpResponse != null

        if(!httpResponse.entity) return null

        return EntityUtils.toByteArray(httpResponse.entity)
    }

    /**
     *
     */
    static class Response {

        Integer httpStatusCode
        String  contentType
        byte[]  contents

        String toString(){
            def stringBuilder = new ReflectionToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            stringBuilder.setExcludeFieldNames(['contents'] as String[])
            return stringBuilder.toString()
        }
    }

    /**
     *
     * @return
     */
    private HttpClient generateHttpClient(){
        return new DefaultHttpClient()
    }

}
