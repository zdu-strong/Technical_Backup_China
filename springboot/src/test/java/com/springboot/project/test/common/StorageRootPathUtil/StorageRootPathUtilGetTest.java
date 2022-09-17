package com.springboot.project.test.common.StorageRootPathUtil;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import com.springboot.project.test.BaseTest;

public class StorageRootPathUtilGetTest extends BaseTest {

    @Test
    public void test() {
        String rootPath = this.storage.getRootPath();
        assertTrue(rootPath.endsWith("target/storage"));
    }
}
