package com.springboot.project.test.controller.UserMessageController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.net.URISyntaxException;
import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import com.springboot.project.model.UserMessageModel;
import com.springboot.project.model.UserModel;
import com.springboot.project.test.BaseTest;

public class UserMessageControllerRecallMessageTest extends BaseTest {
    private String id;
    private String userId;

    @Test
    public void test() throws URISyntaxException {
        var url = new URIBuilder("/user_message/recall").setParameter("id", id).build();
        var response = this.testRestTemplate.postForEntity(url, null, Void.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        var userMessage = this.userMessageService.getUserMessageById(id, userId);
        assertTrue(userMessage.getIsRecall());
    }

    @BeforeEach
    public void beforeEach() throws URISyntaxException {
        this.userId = this.createAccount("zdu.strong@gmail.com").getId();
        var userMessage = new UserMessageModel().setUser(new UserModel().setId(userId)).setContent("Hello, World!");
        this.id = this.userMessageService.sendMessage(userMessage).getId();
    }

}
