package com.springboot.project.test.service.TokenService;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.net.URISyntaxException;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.auth0.jwt.JWT;
import com.fasterxml.uuid.Generators;
import com.springboot.project.model.UserModel;
import com.springboot.project.test.BaseTest;

public class TokenServiceGetPrivateKeyOfRSAOfTokenTest extends BaseTest {
    private UserModel user;
    private String jwtId;

    @Test
    public void test() throws URISyntaxException {
        assertTrue(StringUtils.isNotBlank(this.tokenService.getPrivateKeyOfRSAOfToken(jwtId)));
    }

    @BeforeEach
    public void beforeEach() {
        var email = Generators.timeBasedGenerator().generate().toString() + "zdu.strong@gmail.com";
        this.user = this.createAccount(email);
        this.jwtId = JWT.decode(this.user.getAccess_token()).getId();
    }

}
