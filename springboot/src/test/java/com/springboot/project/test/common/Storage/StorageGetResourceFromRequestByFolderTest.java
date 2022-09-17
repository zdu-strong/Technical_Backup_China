package com.springboot.project.test.common.Storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.mock.web.MockHttpServletRequest;
import com.alibaba.fastjson.JSON;
import com.springboot.project.test.BaseTest;

public class StorageGetResourceFromRequestByFolderTest extends BaseTest {

    private Resource resource;

    @Test
    public void test() throws IOException {
        assertEquals(15, resource.contentLength());
        try (var input = resource.getInputStream()) {
            assertEquals(JSON.toJSONString(new String[] { "default.jpg" }),
                    IOUtils.toString(input, StandardCharsets.UTF_8));
        }
    }

    @BeforeEach
    public void beforeEach() {
        var resource = new UrlResource(ClassLoader.getSystemResource("zip/default.zip"));
        File tempFolder = this.storage.createTempFolderByDecompressingZipResource(resource);
        var mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest
                .setRequestURI(this.storage.storageResource(new FileSystemResource(tempFolder)).getRelativeUrl());
        this.resource = this.storage.getResourceFromRequest(mockHttpServletRequest);
    }
}
