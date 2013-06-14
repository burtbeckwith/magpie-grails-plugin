package com.erasmos.grails.magpie_plugin

import org.apache.commons.lang.builder.ReflectionToStringBuilder
import org.apache.commons.lang.builder.ToStringStyle
import org.apache.http.HttpHost
import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.conn.params.ConnRoutePNames
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.util.EntityUtils
import org.springframework.http.HttpStatus

class FetchService {

    static transactional = false

    def useLocalProxy = false

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

           response =  new Response(httpStatusCode: httpResponse.statusLine.statusCode,
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
     * TODO: Note sure about this one; perhaps
     * there shouldn't be a status code at all.
     *
     * And maybe we should have a connected boolean ...
     *
     * @return
     */
    private Response generateResponseForUnknownHost(){

        new Response(httpStatusCode: 503,
                contentType: null,
                contents: null)

    }


    private String extractContentType(final HttpResponse httpResponse){
        return httpResponse.getHeaders('Content-type').first().value
    }

    private byte[] extractContents(final HttpResponse httpResponse) {
        if(!httpResponse.entity) return null

        return EntityUtils.toByteArray(httpResponse.entity)
    }

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


    private HttpClient generateHttpClient(){

        def httpClient = new DefaultHttpClient()
        if(useLocalProxy){
            httpClient.params.setParameter(ConnRoutePNames.DEFAULT_PROXY, new HttpHost('localhost',8888))
        }

        return httpClient
    }

}
