package com.springboot.project.test.common.PermissionUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.springboot.project.model.TokenModel;
import com.springboot.project.test.BaseTest;

public class PermissionUtilGetUserIdFromAccessTokenTest extends BaseTest {
    private TokenModel tokenModel;

    @Test
    public void test() {
        var userId = this.permissionUtil.getUserId(this.tokenModel.getAccess_token());
        assertTrue(StringUtils.isNotBlank(userId));
        assertEquals(this.tokenModel.getUserModel().getId(), userId);
    }

    @BeforeEach
    public void beforeEach() {
        this.tokenModel = this.createAccount("zdu.strong@gmail.com");
    }
}
