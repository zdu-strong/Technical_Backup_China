package com.springboot.project.test.controller.LongTermTaskController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.net.URISyntaxException;
import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.springboot.project.model.LongTermTaskModel;
import com.springboot.project.test.BaseTest;

public class LongTermTaskControllerGetLongTermTaskTest extends BaseTest {
    private String relativeUrl;

    @Test
    public void test() throws URISyntaxException {
        var url = new URIBuilder(this.relativeUrl).build();
        var result = this.testRestTemplate.getForEntity(url, new LongTermTaskModel<String>().getClass());
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody().getId());
        assertTrue(result.getBody().getIsDone());
        assertEquals("Hello, World!", result.getBody().getResult());
    }

    @BeforeEach
    public void beforeEach() {
        this.relativeUrl = this.longTermTaskUtil.run(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
            return ResponseEntity.ok("Hello, World!");
        }).getBody();
    }
}
