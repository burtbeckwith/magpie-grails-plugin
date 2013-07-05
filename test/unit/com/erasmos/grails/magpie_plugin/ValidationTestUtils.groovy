package com.erasmos.grails.magpie_plugin


class ValidationTestUtils {

    /**
     *
     * @param proposedDomainObject
     * @param fieldName
     * @param expectedCode
     */
    void assertFieldError(final proposedDomainObject, final String fieldName, final String expectedCode) {
        assert expectedCode == proposedDomainObject.errors.getFieldError(fieldName).code
    }
}
