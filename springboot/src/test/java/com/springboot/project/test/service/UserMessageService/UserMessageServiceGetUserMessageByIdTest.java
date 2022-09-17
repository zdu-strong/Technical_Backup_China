package com.springboot.project.test.service.UserMessageService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.net.URISyntaxException;
import com.fasterxml.uuid.Generators;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.springboot.project.model.UserMessageModel;
import com.springboot.project.model.UserModel;
import com.springboot.project.test.BaseTest;

public class UserMessageServiceGetUserMessageByIdTest extends BaseTest {
    private UserMessageModel userMessage;

    @Test
    public void test() throws URISyntaxException {
        var message = this.userMessageService.getUserMessageById(this.userMessage.getId(),
                this.userMessage.getUser().getId());
        assertEquals(this.userMessage.getId(), message.getId());
        assertFalse(message.getIsRecall());
        assertEquals("Hello, World!", message.getContent());
        assertNotNull(message.getCreateDate());
        assertFalse(message.getIsDelete());
        assertFalse(message.getIsRecall());
        assertNull(message.getPageNum());
        assertNull(message.getTotalPage());
        assertNotNull(message.getUpdateDate());
        assertNull(message.getUrl());
        assertTrue(StringUtils.isNotBlank(message.getUser().getId()));
    }

    @BeforeEach
    public void beforeEach() {
        var userId = this.createAccount(Generators.timeBasedGenerator().generate().toString() + "zdu.strong@gmail.com")
                .getUserModel().getId();
        var userMessage = new UserMessageModel().setUser(new UserModel().setId(userId)).setContent("Hello, World!");
        var message = this.userMessageService.sendMessage(userMessage);
        assertEquals(36, message.getId().length());
        assertEquals("Hello, World!", message.getContent());
        assertEquals(userId, message.getUser().getId());
        assertFalse(message.getIsRecall());
        assertFalse(message.getIsDelete());
        this.userMessage = message;
    }
}
