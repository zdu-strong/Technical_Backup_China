package com.springboot.project.test.service.UserMessageService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import java.net.URISyntaxException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.springboot.project.model.UserMessageModel;
import com.springboot.project.model.UserModel;
import com.springboot.project.test.BaseTest;

public class UserMessageServiceSendMessageTest extends BaseTest {
    private UserMessageModel userMessage;

    @Test
    public void test() throws URISyntaxException {
        var result = this.userMessageService.sendMessage(userMessage);
        assertEquals(36, result.getId().length());
        assertEquals("Hello, World!", result.getContent());
        assertEquals(this.userMessage.getUser().getId(), result.getUser().getId());
        assertFalse(result.getIsRecall());
        assertFalse(result.getIsDelete());
    }

    @BeforeEach
    public void beforeEach() {
        var userId = this.createAccount("zdu.strong@gmail.com").getId();
        this.userMessage = new UserMessageModel().setUser(new UserModel().setId(userId)).setContent("Hello, World!");
    }
}
