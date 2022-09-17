package com.springboot.project.test.common.ResourceUtil;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import com.springboot.project.test.BaseTest;

public class ResourceUtilSetETagTest extends BaseTest {
    private HttpHeaders httpHeaders;

    @Test
    public void test() throws IOException {
        this.resourceHttpHeadersUtil.setETag(httpHeaders, request);
        assertTrue(StringUtils.isNotBlank(httpHeaders.getETag()));
    }

    @BeforeEach
    public void beforeEach() {
        httpHeaders = new HttpHeaders();
        var storageFileModel = this.storage
                .storageResource(new UrlResource(ClassLoader.getSystemResource("image/default.jpg")));
        this.request.setRequestURI(storageFileModel.getRelativeUrl());
    }
}
