package com.springboot.project.test.service.TokenService;

import java.net.URISyntaxException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.auth0.jwt.JWT;
import com.fasterxml.uuid.Generators;
import com.springboot.project.model.TokenModel;
import com.springboot.project.test.BaseTest;

public class TokenServiceDeleteTokenEntityTest extends BaseTest {
    private TokenModel tokenModel;
    private String jwtId;

    @Test
    public void test() throws URISyntaxException {
        this.tokenService.deleteTokenEntity(jwtId);
    }

    @BeforeEach
    public void beforeEach() {
        var email = Generators.timeBasedGenerator().generate().toString() + "zdu.strong@gmail.com";
        this.tokenModel = this.createAccount(email);
        this.jwtId = JWT.decode(this.tokenModel.getAccess_token()).getId();
    }

}
