package com.springboot.project.test.controller.ResourceController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.hc.core5.net.URIBuilder;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import com.google.common.collect.Lists;
import com.springboot.project.test.BaseTest;

public class ResourceControllerGetClassPathResourceGetDirectoryTest extends BaseTest {

    private URI url;
    private String pathName;

    @Test
    public void test() throws IOException {
        var response = this.testRestTemplate.getForEntity(url, String[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(13, response.getHeaders().getContentLength());
        assertEquals(this.pathName, response.getHeaders().getContentDisposition().getFilename());
        assertEquals(1, response.getBody().length);
        assertEquals("email.xml", JinqStream.from(Lists.newArrayList(response.getBody())).getOnlyValue());
        assertEquals("[\"email.xml\"]", this.objectMapper.writeValueAsString(response.getBody()));
    }

    @BeforeEach
    public void beforeEach() throws URISyntaxException {
        var relativeUrl = this.storage.storageResource(new ClassPathResource("email/email.xml")).getRelativeUrl();
        this.pathName = JinqStream.from(Lists.newArrayList(relativeUrl.split("/")))
                .skip(2)
                .findFirst().get();
        this.url = new URIBuilder(String.join("/", JinqStream.from(Lists.newArrayList(relativeUrl.split("/")))
                .limit(relativeUrl.split("/").length - 1).toList().toArray(new String[] {}))).build();
    }
}
