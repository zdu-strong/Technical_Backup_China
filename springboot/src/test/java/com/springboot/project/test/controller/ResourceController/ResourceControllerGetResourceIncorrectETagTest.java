package com.springboot.project.test.controller.ResourceController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import com.google.common.collect.Lists;
import com.springboot.project.test.BaseTest;

public class ResourceControllerGetResourceIncorrectETagTest extends BaseTest {

    private String resourceUrl;

    @Test
    public void test() throws URISyntaxException {
        URI url = new URIBuilder(resourceUrl).build();
        var httpHeaders = new HttpHeaders();
        httpHeaders.setRange(Lists.newArrayList(new HttpRange() {

            @Override
            public long getRangeStart(long length) {
                return 0;
            }

            @Override
            public long getRangeEnd(long length) {
                return 0;
            }

            @Override
            public String toString() {
                return "0-0";
            }

        }));
        httpHeaders.setIfNoneMatch("\"IncorrectTag\"");
        var response = this.testRestTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(null, httpHeaders),
                byte[].class);
        assertEquals(HttpStatus.PARTIAL_CONTENT, response.getStatusCode());
        assertEquals(MediaType.IMAGE_JPEG, response.getHeaders().getContentType());
        assertTrue(response.getHeaders().getContentDisposition().isInline());
        assertEquals("default.jpg", response.getHeaders().getContentDisposition().getFilename());
        assertEquals(StandardCharsets.UTF_8, response.getHeaders().getContentDisposition().getCharset());
        assertEquals(1, response.getBody().length);
        assertNotNull(response.getHeaders().getETag());
        assertTrue(response.getHeaders().getETag().startsWith("\""));
        assertTrue(response.getHeaders().getETag().endsWith("\""));
        assertEquals("max-age=604800, no-transform, public", response.getHeaders().getCacheControl());
        assertEquals(1, response.getHeaders().getContentLength());
        assertEquals("bytes 0-0/9287", response.getHeaders().get("Content-Range").stream().findFirst().get());
    }

    @BeforeEach
    public void beforeEach() {
        var storageFileModel = this.storage
                .storageResource(new ClassPathResource("image/default.jpg"));
        this.resourceUrl = storageFileModel.getRelativeUrl();
    }
}
