package com.springboot.project.test.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.net.URISyntaxException;
import org.apache.commons.lang3.StringUtils;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.fasterxml.uuid.Generators;
import com.springboot.project.model.TokenModel;
import com.springboot.project.test.BaseTest;

public class UserMessageServiceGetAccountForSignInTest extends BaseTest {
    private TokenModel tokenModel;

    @Test
    public void test() throws URISyntaxException {
        var result = this.userService.getAccountForSignIn(tokenModel.getUserModel().getId());
        assertTrue(StringUtils.isNotBlank(result.getId()));
        assertTrue(StringUtils.isNotBlank(result.getPublicKeyOfRSA()));
        assertTrue(StringUtils.isNotBlank(result.getUsername()));
        assertTrue(StringUtils.isBlank(result.getEmail()));
        assertTrue(StringUtils.isBlank(result.getPassword()));
        assertTrue(StringUtils.isNotBlank(result.getPrivateKeyOfRSA()));
        assertTrue(result.getHasRegistered());
        assertEquals(1, result.getUserEmailList().size());
        assertTrue(StringUtils
                .isNotBlank(JinqStream.from(result.getUserEmailList()).select(s -> s.getId()).getOnlyValue()));
        assertTrue(StringUtils
                .isNotBlank(JinqStream.from(result.getUserEmailList()).select(s -> s.getEmail()).getOnlyValue()));
    }

    @BeforeEach
    public void beforeEach() {
        var email = Generators.timeBasedGenerator().generate().toString() + "zdu.strong@gmail.com";
        this.tokenModel = this.createAccount(email);
    }

}
