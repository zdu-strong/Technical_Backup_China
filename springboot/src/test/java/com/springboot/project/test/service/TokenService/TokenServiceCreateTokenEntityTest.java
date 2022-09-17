package com.springboot.project.test.service.TokenService;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URISyntaxException;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.fasterxml.uuid.Generators;
import com.springboot.project.model.TokenModel;
import com.springboot.project.test.BaseTest;

public class TokenServiceCreateTokenEntityTest extends BaseTest {
    private TokenModel tokenModel;
    private String jwtId;

    @Test
    public void test() throws URISyntaxException {
        this.tokenService.createTokenEntity(this.jwtId, this.tokenModel.getUserModel().getId(), this.tokenModel.getRSA().getPrivateKeyBase64());
        assertTrue(StringUtils.isNotBlank(this.tokenService.getPrivateKeyOfRSAOfToken(jwtId)));
    }

    @BeforeEach
    public void beforeEach() {
        var email = Generators.timeBasedGenerator().generate().toString() + "zdu.strong@gmail.com";
        this.tokenModel = this.createAccount(email);
        this.jwtId = Generators.timeBasedGenerator().generate().toString();
    }

}
