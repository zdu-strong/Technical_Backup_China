package com.springboot.project.test.common.Storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.File;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import com.springboot.project.test.BaseTest;

public class StorageCreateTempFileFromResourceTest extends BaseTest {
    private Resource resource;

    @Test
    public void test() {
        File tempFile = this.storage.createTempFile(resource);
        assertEquals(9287, tempFile.length());
        assertEquals("default.jpg", tempFile.getName());
    }

    @BeforeEach
    public void beforeEach() {
        this.resource = new ClassPathResource("image/default.jpg");
    }
}
