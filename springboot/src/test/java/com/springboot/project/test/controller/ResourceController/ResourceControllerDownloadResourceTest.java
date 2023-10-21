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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import com.springboot.project.test.BaseTest;

public class ResourceControllerDownloadResourceTest extends BaseTest {

    private String downloadResourceUrl;

    @Test
    public void test() throws URISyntaxException {
        URI url = new URIBuilder(this.downloadResourceUrl).build();
        var response = this.testRestTemplate.getForEntity(url, byte[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.IMAGE_JPEG, response.getHeaders().getContentType());
        assertTrue(response.getHeaders().getContentDisposition().isAttachment());
        assertEquals("default.jpg", response.getHeaders().getContentDisposition().getFilename());
        assertEquals(StandardCharsets.UTF_8, response.getHeaders().getContentDisposition().getCharset());
        assertEquals(9287, response.getBody().length);
        assertNotNull(response.getHeaders().getETag());
        assertTrue(response.getHeaders().getETag().startsWith("\""));
        assertTrue(response.getHeaders().getETag().endsWith("\""));
        assertEquals("max-age=604800, no-transform, public", response.getHeaders().getCacheControl());
        assertEquals(9287, response.getHeaders().getContentLength());
    }

    @BeforeEach
    public void beforeEach() {
        var storageFileModel = this.storage
                .storageResource(new ClassPathResource("image/default.jpg"));
        this.downloadResourceUrl = storageFileModel.getRelativeDownloadUrl();
    }
}
