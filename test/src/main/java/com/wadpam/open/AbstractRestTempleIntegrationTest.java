package com.wadpam.open;

import com.wadpam.open.json.JBaseObject;
import com.wadpam.open.json.JCursorPage;
import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;


/**
 * Base class for GAE, Spring, RestTemplate integration tests.
 * Contains convenience methods and help classes.
 * @author mattiaslevin
 */
public abstract class AbstractRestTempleIntegrationTest {
    static final Logger LOG = LoggerFactory.getLogger(AbstractRestTempleIntegrationTest.class);


    // Spring rest template
    protected RestTemplate restTemplate;

    protected final String BASE_URL = getBaseUrl();

    // All sub classes must implement this method
    protected abstract String getBaseUrl();
    // TODO: If using relative paths, add the base url


    // Basic authentication
    protected String getUserName() {
        return null;
    }

    protected String getPassword() {
        return null;
    }


    @Before
    public void setUp() {
        restTemplate = new RestTemplate(new SimpleAuthClientHttpRequestFactory());

        // Configure an error handler that does not throw exceptions
        // All http codes are handled and tested using asserts
        restTemplate.setErrorHandler(new DoNothingResponseErrorHandler());
    }


    // Custom http request factory that set basic authentication if user and password is set
    protected class SimpleAuthClientHttpRequestFactory extends SimpleClientHttpRequestFactory {
        private final String BASIC_AUTH_PREFIX = "Basic ";
        private final Base64 B64 = new Base64();

        @Override
        public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
            ClientHttpRequest httpRequest = super.createRequest(uri, httpMethod);

            if (null != getUserName() && null != getPassword()) {
                // Set basic authentication
                HttpHeaders headers = httpRequest.getHeaders();
                headers.set("Authorization", encode(getUserName(), getPassword()));
            }

            return httpRequest;
        }

        // Build basic authentication string
        private String encode(String username, String password) {
            final String decoded = String.format("%s:%s", username, password);
            return BASIC_AUTH_PREFIX + B64.encodeAsString(decoded.getBytes());
        }
    }


    // A error handler for Spring RestTemplate that ignores all http error codes.
    protected class DoNothingResponseErrorHandler extends DefaultResponseErrorHandler {
        @Override
        protected boolean hasError(HttpStatus statusCode) {
            return false;
        }
    }


    // Check that the response is a redirect (assert if not).
    // Extract the headers from from the response.
    protected class RedirectResponseExtractor implements ResponseExtractor<URI> {
        @Override
        public URI extractData(ClientHttpResponse clientHttpResponse) throws IOException {

            // Check that it is a redirect
            assertTrue("Redirect", clientHttpResponse.getStatusCode() == HttpStatus.FOUND);

            return clientHttpResponse.getHeaders().getLocation();
        }
    }

    // Extract the status code from the response
    protected class StatusCodeResponseExtractor implements ResponseExtractor<HttpStatus> {
        @Override
        public HttpStatus extractData(ClientHttpResponse clientHttpResponse) throws IOException {

            return clientHttpResponse.getStatusCode();
        }
    }

    // This class writes a map to the request body in a RestTemplate execute method.
    // The reason fro this class is that if you use the provided RestTemplate methods it will
    // not write parameters with the same name more then once.
    protected class RequestCallBackBodyWriter implements RequestCallback {

        MultiValueMap<String, Object> map;

        protected RequestCallBackBodyWriter(MultiValueMap<String, Object> map) {
            this.map = map;
        }

        @Override
        public void doWithRequest(ClientHttpRequest clientHttpRequest) throws IOException {
            StringBuilder builder = new StringBuilder();

            for (Map.Entry<String, List<Object>> entry : map.entrySet()) {
                for (Object value : entry.getValue())
                    builder.append(entry.getKey()).append("=").append(value).append("&");
            }
            LOG.debug("Write POST body:{}", builder);

            PrintWriter out = new PrintWriter(clientHttpRequest.getBody());
            out.write(builder.toString()); // TODO add url encoding
            out.flush();
            out.close();
        }
    }

    // Make a post and follow the redirect
    protected <T> ResponseEntity<T> postAndFollowRedirect(
            String url, MultiValueMap<String, Object> map, Class<T> clazz)
            throws MalformedURLException {

        URI redirectUrl = restTemplate.execute(url, HttpMethod.POST,
                new RequestCallBackBodyWriter(map),
                new RedirectResponseExtractor());
        assertNotNull("Redirect URL", redirectUrl);

        ResponseEntity<T> entity = restTemplate.getForEntity(redirectUrl, clazz);
        assertEquals("Http response 200", HttpStatus.OK, entity.getStatusCode());

        return entity;
    }

    // Make a delete and follow the redirect
    protected  <T> ResponseEntity<T> deleteAndFollowRedirect(
            String url, MultiValueMap<String, Object> map, Class<T> clazz)
            throws MalformedURLException {

        URI redirectUrl = restTemplate.execute(url, HttpMethod.DELETE,
                null,
                new RedirectResponseExtractor());
        assertNotNull("Redirect URL", redirectUrl);

        ResponseEntity<T> entity = restTemplate.getForEntity(redirectUrl, clazz);
        assertEquals("Http response 200", HttpStatus.OK, entity.getStatusCode());

        return entity;
    }

    // Get the http status code of the resource
    protected HttpStatus getResourceStatusCode(URI url) {
        HttpStatus statusCode = restTemplate.execute(url,
                HttpMethod.GET,
                null, // No need to modify the request before sending
                new StatusCodeResponseExtractor());

        return statusCode;
    }

    // Get the http status code of the resource
    protected HttpStatus getResourceStatusCode(String url, Object... urlVariables) {
        HttpStatus statusCode = restTemplate.execute(url,
                HttpMethod.GET,
                null, // No need to modify the request before sending
                new StatusCodeResponseExtractor(),
                urlVariables);

        return statusCode;
    }


    // Check if a resource exist on the given url
    protected boolean doesResourceExist(String url, Object... urlVariables) {
        return getResourceStatusCode(url, urlVariables) == HttpStatus.OK;
    }


    // Delete a resource at the given url
    protected boolean deleteResource(String url, Object ... urlVariables) {
        HttpStatus statusCode = restTemplate.execute(url,
                HttpMethod.DELETE,
                null, // No need to modify the request before sending
                new StatusCodeResponseExtractor(),
                urlVariables);
        assertEquals("Http response 200", HttpStatus.OK, statusCode);

        return true;
    }

    // Delete a resource at the given url and return the JSON response
    protected <T> ResponseEntity<T> deleteResource(String url, Class<T> clazz, Object ... urlVariables) {
        ResponseEntity<T> response = restTemplate.exchange(url,
                HttpMethod.DELETE,
                null,
                clazz,
                urlVariables);
        assertEquals("Http response 200", HttpStatus.OK, response.getStatusCode());

        return response;
    }

    // Delete a resource and verify that it was deleted
    protected boolean deleteResourceAndCheck(String url, Object... urlVariables) {
        deleteResource(url, urlVariables);
        assertFalse("Resource deleted", doesResourceExist(url, urlVariables));

        return true;
    }

    // Count the number of entities at the resource
    protected int countResources(String url, Object... urlVariables) {
        ResponseEntity<Collection> entity =
                restTemplate.getForEntity(url, Collection.class, urlVariables);
        assertEquals("Http response 200", HttpStatus.OK, entity.getStatusCode());

        return entity.getBody().size();
    }

    // Count the number of entities at the result page
    protected <T extends JBaseObject> int countResourcesInPage(String url,  Class<T> clazz, Object... urlVariables) {
        ParameterizedTypeReference<JCursorPage<T>> jCursorPage =
                new ParameterizedTypeReference<JCursorPage<T>>() {};

        ResponseEntity<JCursorPage<T>> entity = restTemplate.exchange(url,
                HttpMethod.GET,
                null,
                jCursorPage,
                urlVariables);
        assertEquals("Http response 200", HttpStatus.OK, entity.getStatusCode());

        return entity.getBody().getItems().size();
    }


    // TODO Create builder

}
