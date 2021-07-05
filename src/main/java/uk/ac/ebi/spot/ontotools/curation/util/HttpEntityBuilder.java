package uk.ac.ebi.spot.ontotools.curation.util;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.util.Map;

public class HttpEntityBuilder {

    private Object body;

    private MediaType contentType;

    private HttpHeaders headers;

    public HttpEntityBuilder(String serverName) {
        this.headers = new HttpHeaders();
    }

    public HttpEntityBuilder withHeaders(Map<String, String> headerMap) {
        headers.setAll(headerMap);
        return this;
    }

    public HttpEntityBuilder withJsonBody(Object body) {
        this.body = body;
        this.contentType = MediaType.APPLICATION_JSON;
        return this;
    }

    public HttpEntityBuilder withStringBody(String body) {
        this.body = body;
        this.contentType = MediaType.TEXT_PLAIN;
        return this;
    }

    public HttpEntityBuilder withMultipartBody(Object body) {
        this.body = body;
        this.contentType = MediaType.MULTIPART_FORM_DATA;
        return this;
    }

    public HttpEntity build() {
        if (body != null) {
            headers.setContentType(contentType);
        }

        return new HttpEntity(body, headers);
    }
}
