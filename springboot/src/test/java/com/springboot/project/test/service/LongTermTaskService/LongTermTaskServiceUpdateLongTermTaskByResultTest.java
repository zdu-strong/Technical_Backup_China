package com.springboot.project.test.service.LongTermTaskService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.springboot.project.model.LongTermTaskModel;
import com.springboot.project.test.BaseTest;

public class LongTermTaskServiceUpdateLongTermTaskByResultTest extends BaseTest {
    private String longTermtaskId;

    @Test
    public void test() {
        this.longTermTaskService.updateLongTermTaskByResult(this.longTermtaskId, ResponseEntity.ok("Hello, World!"));
        var longTermTask = this.longTermTaskService.getLongTermTask(this.longTermtaskId);
        assertEquals(HttpStatus.OK, longTermTask.getStatusCode());
        assertEquals("Hello, World!", ((LongTermTaskModel<?>) longTermTask.getBody()).getResult());
        assertTrue(((LongTermTaskModel<?>) longTermTask.getBody()).getIsDone());
    }

    @BeforeEach
    public void BeforeEach() throws InterruptedException {
        this.longTermtaskId = this.longTermTaskService.createLongTermTask();
    }

}
