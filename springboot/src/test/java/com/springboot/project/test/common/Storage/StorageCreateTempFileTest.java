package com.springboot.project.test.common.Storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.File;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.UrlResource;
import com.springboot.project.test.BaseTest;

public class StorageCreateTempFileTest extends BaseTest {
    private String resourcePath;

    @Test
    public void test() {
        File tempFile = this.storage.createTempFileOrFolder(resourcePath);
        assertEquals(9287, tempFile.length());
        assertEquals("default.jpg", tempFile.getName());
    }

    @BeforeEach
    public void beforeEach() {
        this.resourcePath = this.storage
                .storageResource(new UrlResource(ClassLoader.getSystemResource("image/default.jpg")))
                .getRelativePath();
    }
}
