package com.springboot.project.test.service.UserService;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.net.URISyntaxException;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.fasterxml.uuid.Generators;
import com.springboot.project.model.TokenModel;
import com.springboot.project.test.BaseTest;

public class UserMessageServiceGetUserByIdTest extends BaseTest {
    private TokenModel tokenModel;

    @Test
    public void test() throws URISyntaxException {
        var result = this.userService.getUserById(tokenModel.getUserModel().getId());
        assertTrue(StringUtils.isNotBlank(result.getId()));
        assertTrue(StringUtils.isNotBlank(result.getPublicKeyOfRSA()));
        assertTrue(StringUtils.isNotBlank(result.getUsername()));
        assertTrue(StringUtils.isBlank(result.getEmail()));
        assertTrue(StringUtils.isBlank(result.getPassword()));
        assertTrue(StringUtils.isBlank(result.getPrivateKeyOfRSA()));
        assertTrue(result.getHasRegistered());
        assertNull(result.getUserEmailList());
    }

    @BeforeEach
    public void beforeEach() {
        var email = Generators.timeBasedGenerator().generate().toString() + "zdu.strong@gmail.com";
        this.tokenModel = this.createAccount(email);
    }

}

