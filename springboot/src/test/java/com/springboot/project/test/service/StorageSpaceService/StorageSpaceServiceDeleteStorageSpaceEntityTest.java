package com.springboot.project.test.service.StorageSpaceService;

import static org.junit.jupiter.api.Assertions.assertTrue;
import com.fasterxml.uuid.Generators;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import com.springboot.project.test.BaseTest;
import io.reactivex.Observable;

public class StorageSpaceServiceDeleteStorageSpaceEntityTest extends BaseTest {
    private String folderName = Generators.timeBasedGenerator().generate().toString();

    @Test
    public void test() {
        this.storageSpaceService.deleteStorageSpaceEntity(folderName);
        var totalPage = this.storageSpaceService.getStorageSpaceListByPagination(1, 1).getTotalPage();
        var list = Observable.interval(0, TimeUnit.SECONDS).take(totalPage).concatMap((s) -> {
            int pageNum = s.intValue() + 1;
            return Observable
                    .fromIterable(this.storageSpaceService.getStorageSpaceListByPagination(pageNum, 1).getList());
        }).toList().blockingGet();
        assertTrue(list.stream().filter(s -> s.getFolderName().equals(folderName)).findFirst().isPresent());
    }
}
