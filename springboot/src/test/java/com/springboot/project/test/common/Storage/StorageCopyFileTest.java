package com.springboot.project.test.common.Storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.UrlResource;
import com.springboot.project.test.BaseTest;

public class StorageCopyFileTest extends BaseTest {

    @Test
    public void test() {
        var storageFileModel = this.storage
                .storageResource(new UrlResource(ClassLoader.getSystemResource("image/default.jpg")));
        assertEquals(9287, this.storage.createTempFileOrFolder(storageFileModel.getRelativePath()).length());
        assertEquals("default.jpg",
                this.storage.createTempFileOrFolder(storageFileModel.getRelativePath()).getName());
        assertFalse(this.storage.createTempFileOrFolder(storageFileModel.getRelativePath()).isDirectory());
        assertTrue(this.storage.createTempFileOrFolder(storageFileModel.getRelativePath()).isFile());
    }
}
