package com.springboot.project.test.controller.LongTermTaskController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.net.URISyntaxException;
import java.util.List;
import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.google.common.collect.Lists;
import com.springboot.project.model.LongTermTaskModel;
import com.springboot.project.test.BaseTest;

public class LongTermTaskControllerGetLongTermTaskReturnArrayTest extends BaseTest {
    private String relativeUrl;

    @Test
    public void test() throws URISyntaxException {
        var url = new URIBuilder(this.relativeUrl).build();
        var result = this.testRestTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(null),
                new ParameterizedTypeReference<LongTermTaskModel<List<String>>>() {
                });
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody().getId());
        assertTrue(result.getBody().getIsDone());
        assertEquals(2, result.getBody().getResult().size());
        assertEquals("Hello, World!", result.getBody().getResult().get(0));
        assertEquals("I love China", result.getBody().getResult().get(1));
        assertEquals(result.getHeaders().get("my custom headers").size(), 1);
        assertEquals(result.getHeaders().get("my custom headers").get(0), "Hello, World!");
    }

    @BeforeEach
    public void beforeEach() {
        this.relativeUrl = this.longTermTaskUtil.run(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e.getMessage(), e);
            }

            var httpHeaders = new HttpHeaders();
            httpHeaders.addAll("my custom headers", Lists.newArrayList("Hello, World!"));
            return ResponseEntity.ok().headers(httpHeaders).body(new String[] { "Hello, World!", "I love China" });
        }).getBody();
    }
}
