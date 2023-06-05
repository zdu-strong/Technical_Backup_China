package com.springboot.project.test.common.Storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import com.springboot.project.test.BaseTest;

public class StorageGetResoureUrlFromResourcePathTest extends BaseTest {
    private String resoucePath;

    @Test
    public void test() throws IOException {
        String resourceUrl = this.storage.getResoureUrlFromResourcePath(resoucePath);
        assertNotNull(resourceUrl);
        this.request.setRequestURI(resourceUrl);
        assertEquals(9287, this.storage.getResourceFromRequest(request).contentLength());
        assertEquals("default.jpg", this.storage.getResourceFromRequest(request).getFilename());
    }

    @BeforeEach
    public void beforeEach() {
        this.resoucePath = this.storage
                .storageResource(new ClassPathResource("image/default.jpg")).getRelativePath();
    }
}
