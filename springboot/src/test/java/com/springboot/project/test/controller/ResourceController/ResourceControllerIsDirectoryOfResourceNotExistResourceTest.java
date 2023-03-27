package com.springboot.project.test.controller.ResourceController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.http.client.utils.URIBuilder;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import com.springboot.project.test.BaseTest;

public class ResourceControllerIsDirectoryOfResourceNotExistResourceTest extends BaseTest {

    private String resourceUrl;

    @Test
    public void test() throws URISyntaxException {
        URI url = new URIBuilder(resourceUrl).build();
        var response = this.testRestTemplate.getForEntity(url, Boolean.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody());
    }

    @BeforeEach
    public void beforeEach() throws URISyntaxException {
        var storageFileModel = this.storage
                .storageResource(new UrlResource(ClassLoader.getSystemResource("image/default.jpg")));
        this.resourceUrl = "/is_directory/" + String.join("/",
                JinqStream.from(new URIBuilder(storageFileModel.getRelativeUrl()).getPathSegments()).limit(2)
                        .toArray(String[]::new))
                + "/not_exist_file.txt";
    }
}
