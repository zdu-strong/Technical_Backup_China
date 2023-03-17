package com.springboot.project.test.common.ResourceUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import com.springboot.project.test.BaseTest;

public class ResourceUtilSetContentDispositionTest extends BaseTest {
    private HttpHeaders httpHeaders;

    @Test
    public void test() throws IOException {
        this.resourceHttpHeadersUtil.setContentDisposition(httpHeaders, ContentDisposition.inline(),
                this.storage.getResourceFromRequest(request), request);
        assertEquals("inline; filename=\"=?UTF-8?Q?default.jpg?=\"; filename*=UTF-8''default.jpg",
                this.httpHeaders.getContentDisposition().toString());
        assertTrue(this.httpHeaders.getContentDisposition().toString().contains(" filename*=UTF-8''default.jpg"));
        assertTrue(this.httpHeaders.getContentDisposition().toString().contains("inline;"));
        assertEquals("inline", this.httpHeaders.getContentDisposition().getType());
        assertEquals("default.jpg", this.httpHeaders.getContentDisposition().getFilename());
        assertEquals(StandardCharsets.UTF_8, this.httpHeaders.getContentDisposition().getCharset());
    }

    @BeforeEach
    public void beforeEach() {
        httpHeaders = new HttpHeaders();
        var storageFileModel = this.storage
                .storageResource(new UrlResource(ClassLoader.getSystemResource("image/default.jpg")));
        this.request.setRequestURI(storageFileModel.getRelativeUrl());
    }
}
