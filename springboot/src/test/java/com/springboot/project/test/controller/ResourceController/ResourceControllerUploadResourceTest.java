package com.springboot.project.test.controller.ResourceController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import com.springboot.project.test.BaseTest;

public class ResourceControllerUploadResourceTest extends BaseTest {

    @Test
    public void test() throws URISyntaxException {
        var url = new URIBuilder("/upload/resource").build();
        var body = new LinkedMultiValueMap<Object, Object>();
        body.set("file", new ClassPathResource("image/default.jpg"));
        var response = this.testRestTemplate.postForEntity(url, body, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().startsWith("/resource/"));
        var result = this.testRestTemplate.getForEntity(new URIBuilder(response.getBody()).build(), byte[].class);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(MediaType.IMAGE_JPEG, result.getHeaders().getContentType());
        assertTrue(result.getHeaders().getContentDisposition().isInline());
        assertEquals("default.jpg", result.getHeaders().getContentDisposition().getFilename());
        assertEquals(StandardCharsets.UTF_8, result.getHeaders().getContentDisposition().getCharset());
        assertEquals(9287, result.getBody().length);
        assertNotNull(result.getHeaders().getETag());
        assertTrue(result.getHeaders().getETag().startsWith("\""));
        assertTrue(result.getHeaders().getETag().endsWith("\""));
        assertEquals("max-age=604800, no-transform, public", result.getHeaders().getCacheControl());
        assertEquals(9287, result.getHeaders().getContentLength());
    }
}
