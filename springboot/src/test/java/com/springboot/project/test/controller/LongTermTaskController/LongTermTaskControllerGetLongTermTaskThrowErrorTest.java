package com.springboot.project.test.controller.LongTermTaskController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.net.URISyntaxException;
import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.databind.JsonNode;
import com.springboot.project.test.BaseTest;

public class LongTermTaskControllerGetLongTermTaskThrowErrorTest extends BaseTest {
    private String relativeUrl;

    @Test
    public void test() throws URISyntaxException {
        var url = new URIBuilder(relativeUrl).build();
        var result = this.testRestTemplate.getForEntity(url, JsonNode.class);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals("Failed due to insufficient funds", result.getBody().get("message").asText());
    }

    @BeforeEach
    public void beforeEach() throws URISyntaxException, InterruptedException {
        this.relativeUrl = this.fromLongTermTask(() -> this.longTermTaskUtil.run(() -> {
            throw new RuntimeException("Failed due to insufficient funds");
        }).getBody());
    }
}
