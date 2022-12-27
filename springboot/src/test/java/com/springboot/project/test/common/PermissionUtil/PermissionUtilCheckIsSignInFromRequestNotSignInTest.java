package com.springboot.project.test.common.PermissionUtil;

import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import com.springboot.project.test.BaseTest;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PermissionUtilCheckIsSignInFromRequestNotSignInTest extends BaseTest {
    @Test
    public void test() {
        assertThrows(ResponseStatusException.class, () -> {
            this.permissionUtil.checkIsSignIn(this.request);
        });
    }
}
