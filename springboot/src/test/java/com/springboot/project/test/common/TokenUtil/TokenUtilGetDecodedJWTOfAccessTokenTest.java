package com.springboot.project.test.common.TokenUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.springboot.project.model.TokenModel;
import com.springboot.project.test.BaseTest;

public class TokenUtilGetDecodedJWTOfAccessTokenTest extends BaseTest {
    private TokenModel tokenModel;

    @Test
    public void test() {
        var decodedJWT = this.tokenUtil.getDecodedJWTOfAccessToken(this.tokenModel.getAccess_token());
        assertNotNull(decodedJWT);
        assertTrue(StringUtils.isNotBlank(decodedJWT.getId()));
        assertTrue(StringUtils.isNotBlank(decodedJWT.getSubject()));
        assertNotNull(decodedJWT.getIssuedAt());
        assertEquals(tokenModel.getUserModel().getId(), decodedJWT.getSubject());
    }

    @BeforeEach
    public void beforeEach() {
        this.tokenModel = this.createAccount("zdu.strong@gmail.com");
    }
}
