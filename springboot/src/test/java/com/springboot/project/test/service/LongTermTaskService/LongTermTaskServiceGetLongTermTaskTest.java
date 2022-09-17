package com.springboot.project.test.service.LongTermTaskService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.springboot.project.model.LongTermTaskModel;
import com.springboot.project.test.BaseTest;

public class LongTermTaskServiceGetLongTermTaskTest extends BaseTest {

    private String longTermtaskId;

    @Test
    public void test() {
        @SuppressWarnings("unchecked")
        var longTermTask = (ResponseEntity<LongTermTaskModel<?>>) this.longTermTaskService
                .getLongTermTask(this.longTermtaskId);
        assertNotNull(longTermTask);
        assertEquals(HttpStatus.ACCEPTED, longTermTask.getStatusCode());
        assertNotNull(longTermTask.getBody().getId());
        assertFalse(longTermTask.getBody().getIsDone());
    }

    @BeforeEach
    public void BeforeEach() throws InterruptedException {
        this.longTermtaskId = this.longTermTaskService.createLongTermTask();
    }

}
