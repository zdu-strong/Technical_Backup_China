package com.springboot.project.test.common.Storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import com.springboot.project.test.BaseTest;

public class StorageGetResourceFromRequestTest extends BaseTest {
    @Test
    public void test() throws IOException {
        Resource resource = this.storage.getResourceFromRequest(request);
        assertEquals(9287, resource.contentLength());
        assertEquals("default.jpg", this.storage.getFileNameFromResource(resource));
    }

    @BeforeEach
    public void beforeEach() {
        var storageFileModel = this.storage
                .storageResource(new UrlResource(ClassLoader.getSystemResource("image/default.jpg")));
        this.request.setRequestURI(storageFileModel.getRelativeUrl());
    }
}
