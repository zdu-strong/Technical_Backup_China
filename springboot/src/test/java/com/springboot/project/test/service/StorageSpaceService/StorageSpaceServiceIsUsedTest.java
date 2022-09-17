package com.springboot.project.test.service.StorageSpaceService;

import static org.junit.jupiter.api.Assertions.assertTrue;
import com.fasterxml.uuid.Generators;
import org.junit.jupiter.api.Test;
import com.springboot.project.test.BaseTest;

public class StorageSpaceServiceIsUsedTest extends BaseTest {
    private String folderName = Generators.timeBasedGenerator().generate().toString();

    @Test
    public void test() {
        var isUsed = this.storageSpaceService.isUsed(folderName);
        assertTrue(isUsed);
    }
}
