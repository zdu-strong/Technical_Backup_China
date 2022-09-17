package com.springboot.project.test.scheduled.StorageSpaceScheduled;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.File;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.FileUtils;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.UrlResource;
import com.springboot.project.test.BaseTest;
import io.reactivex.rxjava3.core.Observable;

public class StorageSpaceScheduledScheduledTest extends BaseTest {
    private String folderName;

    @Test
    public void test() {
        this.storageSpaceScheduled.scheduled();
        var totalPage = this.storageSpaceService.getStorageSpaceListByPagination(1, 1).getTotalPage();
        var list = Observable.interval(0, TimeUnit.SECONDS).take(totalPage).concatMap((s) -> {
            var pageNum = s.intValue() + 1;
            return Observable
                    .fromIterable(this.storageSpaceService.getStorageSpaceListByPagination(pageNum, 1).getList());
        }).toList().blockingGet();
        assertTrue(JinqStream.from(list).where(s -> s.getFolderName().equals(folderName)).exists());
        assertEquals(36,
                JinqStream.from(list).where(s -> s.getFolderName().equals(folderName)).findFirst().get().getId().length());
        assertEquals(folderName,
                JinqStream.from(list).where(s -> s.getFolderName().equals(folderName)).findFirst().get().getFolderName());
        assertNotNull(
                JinqStream.from(list).where(s -> s.getFolderName().equals(folderName)).findFirst().get().getCreateDate());
        assertNotNull(
                JinqStream.from(list).where(s -> s.getFolderName().equals(folderName)).findFirst().get().getUpdateDate());
    }

    @BeforeEach
    public void beforeEach() {
        FileUtils.deleteQuietly(new File(this.storage.getRootPath()));
        var storageFileModel = this.storage
                .storageResource(new UrlResource(ClassLoader.getSystemResource("image/default.jpg")));
        this.folderName = storageFileModel.getFolderName();
    }

}
