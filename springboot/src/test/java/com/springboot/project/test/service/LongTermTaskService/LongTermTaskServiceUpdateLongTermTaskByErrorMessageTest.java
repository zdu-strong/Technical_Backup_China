package com.springboot.project.test.service.LongTermTaskService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.databind.JsonNode;
import com.springboot.project.test.BaseTest;

public class LongTermTaskServiceUpdateLongTermTaskByErrorMessageTest extends BaseTest {
    private String longTermtaskId;

    @Test
    public void test() {
        this.longTermTaskService.updateLongTermTaskByErrorMessage(this.longTermtaskId,
                new RuntimeException("Internal Server Error"));
        var longTermTask = this.longTermTaskService.getLongTermTask(this.longTermtaskId);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, longTermTask.getStatusCode());
        assertEquals("Internal Server Error", ((JsonNode) ((Object) longTermTask.getBody())).get("message").asText());
    }

    @BeforeEach
    public void BeforeEach() throws InterruptedException {
        this.longTermtaskId = this.longTermTaskService.createLongTermTask();
    }

}
