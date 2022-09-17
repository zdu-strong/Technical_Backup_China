package com.springboot.project.test.service.LongTermTaskService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import com.alibaba.fastjson.JSONObject;
import com.springboot.project.test.BaseTest;

public class LongTermTaskServiceUpdateLongTermTaskByErrorMessageThrowResponseStatusExceptionTest extends BaseTest {
    private String longTermtaskId;

    @Test
    public void test() {
        this.longTermTaskService.updateLongTermTaskByErrorMessage(this.longTermtaskId,
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Request"));
        var longTermTask = this.longTermTaskService.getLongTermTask(this.longTermtaskId);
        assertEquals(HttpStatus.BAD_REQUEST, longTermTask.getStatusCode());
        assertEquals("Bad Request", ((JSONObject) ((Object) longTermTask.getBody())).getString("message"));
    }

    @BeforeEach
    public void BeforeEach() throws InterruptedException {
        this.longTermtaskId = this.longTermTaskService.createLongTermTask();
    }

}
