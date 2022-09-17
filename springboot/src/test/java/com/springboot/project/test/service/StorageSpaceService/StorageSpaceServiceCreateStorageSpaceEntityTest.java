package com.springboot.project.test.service.StorageSpaceService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.net.URISyntaxException;
import com.fasterxml.uuid.Generators;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import com.springboot.project.test.BaseTest;

public class StorageSpaceServiceCreateStorageSpaceEntityTest extends BaseTest {
    private String folderName = Generators.timeBasedGenerator().generate().toString();

    @Test
    public void test() throws URISyntaxException {
        var storageSpaceModel = this.storageSpaceService.createStorageSpaceEntity(folderName);
        assertEquals(36, storageSpaceModel.getFolderName().length());
        assertEquals(36, storageSpaceModel.getId().length());
        assertNotNull(storageSpaceModel.getCreateDate());
    }

    @AfterEach
    public void afterEach() {
        this.storageSpaceService.deleteStorageSpaceEntity(folderName);
    }
}
