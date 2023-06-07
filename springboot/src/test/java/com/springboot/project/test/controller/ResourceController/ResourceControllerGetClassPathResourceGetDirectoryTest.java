package com.springboot.project.test.controller.ResourceController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.lang3.StringUtils;
import org.apache.hc.core5.net.URIBuilder;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.springboot.project.common.ClassPathStorage.ClassPathStorageEnum;
import com.springboot.project.test.BaseTest;

public class ResourceControllerGetClassPathResourceGetDirectoryTest extends BaseTest {

    private URI url;
    private String pathName;

    @Test
    public void test() throws IOException {
        var response = this.testRestTemplate.getForEntity(url, String[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(14, response.getHeaders().getContentLength());
        assertEquals(this.pathName, response.getHeaders().getContentDisposition().getFilename());
        assertEquals(1, response.getBody().length);
        assertEquals("email.html", JinqStream.from(Lists.newArrayList(response.getBody())).getOnlyValue());
        assertEquals("[\"email.html\"]", new Gson().toJson(response.getBody()));
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
