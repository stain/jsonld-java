package com.github.jsonldjava.utils;

import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.cache.CacheResponseStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.cache.CachingHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.junit.Ignore;
import org.junit.Test;

public class JsonUtilsTest {
	
    @SuppressWarnings("unchecked")
    @Test
    public void fromStringTest() {
        final String testString = "{\"seq\":3,\"id\":\"e48dfa735d9fad88db6b7cd696002df7\",\"changes\":[{\"rev\":\"2-6aebf275bc3f29b67695c727d448df8e\"}]}";
        final String testFailure = "{{{{{{{{{{{";
        Object obj = null;

        try {
            obj = JsonUtils.fromString(testString);
        } catch (final Exception e) {
            assertTrue(false);
        }

        assertTrue(((Map<String, Object>) obj).containsKey("seq"));
        assertTrue(((Map<String, Object>) obj).get("seq") instanceof Number);

        try {
            obj = JsonUtils.fromString(testFailure);
            assertTrue(false);
        } catch (final Exception e) {
            assertTrue(true);
        }
    }

}
