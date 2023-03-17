package com.springboot.project.test.common.PermissionUtil;

import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.Test;
import com.springboot.project.test.BaseTest;

public class PermissionUtilIsSignFromRequestInNotSignInTest extends BaseTest {
    @Test
    public void test() {
        var isSignIn = this.permissionUtil.isSignIn(this.request);
        assertFalse(isSignIn);
    }

}
