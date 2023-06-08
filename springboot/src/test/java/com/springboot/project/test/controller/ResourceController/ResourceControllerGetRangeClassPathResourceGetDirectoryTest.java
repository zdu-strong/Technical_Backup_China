package com.springboot.project.test.controller.ResourceController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.hc.core5.net.URIBuilder;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import com.google.common.collect.Lists;
import com.springboot.project.test.BaseTest;

public class ResourceControllerGetRangeClassPathResourceGetDirectoryTest extends BaseTest {

    private URI url;
    private String pathName;

    @Test
    public void test() throws IOException {
        var httpHeaders = new HttpHeaders();
        httpHeaders.setRange(Lists.newArrayList(HttpRange.createByteRange(0, 10)));
        var response = this.testRestTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(httpHeaders),
                byte[].class);
        assertEquals(HttpStatus.PARTIAL_CONTENT, response.getStatusCode());
        assertEquals(11, response.getHeaders().getContentLength());
        assertEquals(this.pathName, response.getHeaders().getContentDisposition().getFilename());
        assertEquals(11, response.getBody().length);
        assertEquals(Integer.valueOf(91).byteValue(),
                JinqStream.from(Lists.newArrayList(ArrayUtils.toObject(response.getBody()))).findFirst().get());
        assertEquals("[\"email.xml", IOUtils.toString(response.getBody(), StandardCharsets.UTF_8.name()));
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
