package com.springboot.project.test.common.TokenUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.springboot.project.model.TokenModel;
import com.springboot.project.test.BaseTest;

public class TokenUtilGenerateAccessTokenTest extends BaseTest {
    private TokenModel tokenModel;

    @Test
    public void test() {
        var accessToken = this.tokenUtil.generateAccessToken(this.tokenModel.getUserModel().getId(),
                this.tokenModel.getRSA().getPrivateKeyBase64());
        assertTrue(StringUtils.isNotBlank(accessToken));
        assertNotNull(this.tokenUtil.getDecodedJWTOfAccessToken(accessToken).getId());
        assertEquals(tokenModel.getUserModel().getId(),
                this.tokenUtil.getDecodedJWTOfAccessToken(accessToken).getSubject());
        assertNotNull(this.tokenUtil.getDecodedJWTOfAccessToken(accessToken).getIssuedAt());
    }

    @BeforeEach
    public void beforeEach() {
        this.tokenModel = this.createAccount("zdu.strong@gmail.com");
    }
}
