package com.springboot.project.test.common.Storage;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.UrlResource;
import com.springboot.project.test.BaseTest;

public class StorageCreateTempFolderFromResourcePathTest extends BaseTest {

    private String resourcePath;

    @Test
    public void test() {
        File tempFolder = this.storage.createTempFileOrFolder(resourcePath);
        assertEquals(9287, FileUtils.sizeOfDirectory(tempFolder));
        assertEquals("default.jpg", new File(tempFolder, "default.jpg").getName());
        assertEquals(9287, new File(tempFolder, "default.jpg").length());
    }

    @BeforeEach
    public void beforeEach() {
        File tempFolder = this.storage.createTempFolderByDecompressingZipResource(
                new UrlResource(ClassLoader.getSystemResource("zip/default.zip")));
        this.resourcePath = this.storage.storageResource(new FileSystemResource(tempFolder)).getRelativePath();
    }
}
