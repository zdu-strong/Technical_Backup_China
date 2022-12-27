package com.springboot.project.test.common.Storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.multipart.MultipartFile;
import com.springboot.project.test.BaseTest;

public class StorageCreateTempMultipartFileTest extends BaseTest {
    private Resource resource;

    @Test
    public void test() {
        MultipartFile tempMultipartFile = this.createTempMultipartFile(resource);
        assertEquals(9287, tempMultipartFile.getSize());
        assertEquals("default.jpg", tempMultipartFile.getName());
    }

    @BeforeEach
    public void beforeEach() {
        this.resource = new UrlResource(ClassLoader.getSystemResource("image/default.jpg"));
    }
}
