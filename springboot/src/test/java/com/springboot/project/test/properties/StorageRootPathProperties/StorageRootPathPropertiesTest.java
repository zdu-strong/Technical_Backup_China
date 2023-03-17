package com.springboot.project.test.properties.StorageRootPathProperties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import com.springboot.project.test.BaseTest;

public class StorageRootPathPropertiesTest extends BaseTest {

    @Test
    public void test() {
        assertEquals("defaultTest-a56b075f-102e-edf3-8599-ffc526ec948a",
                this.storageRootPathProperties.getStorageRootPath());
    }
}
