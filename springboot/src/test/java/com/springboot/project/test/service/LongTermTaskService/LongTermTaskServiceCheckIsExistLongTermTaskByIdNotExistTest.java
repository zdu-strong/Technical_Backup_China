package com.springboot.project.test.service.LongTermTaskService;

import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import com.fasterxml.uuid.Generators;
import com.springboot.project.test.BaseTest;

public class LongTermTaskServiceCheckIsExistLongTermTaskByIdNotExistTest extends BaseTest {

    private String longTermtaskId;

    @Test
    public void test() {
        assertThrowsExactly(ResponseStatusException.class, () -> {
            this.longTermTaskService.checkIsExistLongTermTaskById(this.longTermtaskId);
        });

    }

    @BeforeEach
    public void BeforeEach() throws InterruptedException {
        this.longTermtaskId = Generators.timeBasedGenerator().generate().toString();
    }

}
