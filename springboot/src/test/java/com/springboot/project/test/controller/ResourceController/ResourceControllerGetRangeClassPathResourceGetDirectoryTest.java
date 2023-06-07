package com.springboot.project.test.controller.ResourceController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.core5.net.URIBuilder;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import com.google.common.collect.Lists;
import com.springboot.project.common.ClassPathStorage.ClassPathStorageEnum;
import com.springboot.project.test.BaseTest;

public class ResourceControllerGetRangeClassPathResourceGetDirectoryTest extends BaseTest {

    private URI url;
    private String pathName;

    @Test
    public void test() throws IOException {
        var httpHeaders = new HttpHeaders();
        httpHeaders.setRange(Lists.newArrayList(HttpRange.createByteRange(0, 11)));
        var response = this.testRestTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(httpHeaders),
                byte[].class);
        assertEquals(HttpStatus.PARTIAL_CONTENT, response.getStatusCode());
        assertEquals(12, response.getHeaders().getContentLength());
        assertEquals(this.pathName, response.getHeaders().getContentDisposition().getFilename());
        assertEquals(12, response.getBody().length);
        assertEquals(Integer.valueOf(91).byteValue(),
                JinqStream.from(Lists.newArrayList(ArrayUtils.toObject(response.getBody()))).findFirst().get());
        assertEquals("[\"email.html", IOUtils.toString(response.getBody(), StandardCharsets.UTF_8.name()));
    }

    @BeforeEach
    public void beforeEach() throws URISyntaxException {
        this.url = new URIBuilder(this.storage.getResoureUrlFromResourcePath(JinqStream
                .from(Lists.newArrayList(ClassPathStorageEnum.EMAIL_TEMPLATE_FILE.getRelativePath().split("/")))
                .findFirst().get())).build();
        this.pathName = JinqStream.from(Lists.newArrayList(this.storage.getResoureUrlFromResourcePath(JinqStream
                .from(Lists.newArrayList(ClassPathStorageEnum.EMAIL_TEMPLATE_FILE.getRelativePath().split("/")))
                .findFirst().get()).split("/"))).where(s -> StringUtils.isNotBlank(s)).skip(1).getOnlyValue();
    }
}
