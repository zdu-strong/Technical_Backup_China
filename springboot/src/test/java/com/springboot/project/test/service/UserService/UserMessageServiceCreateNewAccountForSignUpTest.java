package com.springboot.project.test.service.UserService;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.net.URISyntaxException;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import com.springboot.project.test.BaseTest;

public class UserMessageServiceCreateNewAccountForSignUpTest extends BaseTest {

    @Test
    public void test() throws URISyntaxException {
        var result = this.userService.createNewAccountForSignUp();
        assertTrue(StringUtils.isNotBlank(result.getId()));
        assertTrue(StringUtils.isNotBlank(result.getPublicKeyOfRSA()));
        assertTrue(StringUtils.isBlank(result.getUsername()));
        assertTrue(StringUtils.isBlank(result.getEmail()));
        assertTrue(StringUtils.isBlank(result.getPassword()));
        assertTrue(StringUtils.isBlank(result.getPrivateKeyOfRSA()));
        assertFalse(result.getHasRegistered());
        assertNull(result.getUserEmailList());
    }

}
