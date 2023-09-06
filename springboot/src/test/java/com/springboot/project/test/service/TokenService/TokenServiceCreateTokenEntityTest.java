package com.springboot.project.test.service.TokenService;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.net.URISyntaxException;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.fasterxml.uuid.Generators;
import com.springboot.project.model.UserModel;
import com.springboot.project.test.BaseTest;

public class TokenServiceCreateTokenEntityTest extends BaseTest {
    private UserModel user;
    private String jwtId;

    @Test
    public void test() throws URISyntaxException {
        this.tokenService.createTokenEntity(this.jwtId, this.user.getId(), this.user.getPrivateKeyOfRSA());
        assertTrue(StringUtils.isNotBlank(this.tokenService.getPrivateKeyOfRSAOfToken(jwtId)));
    }

    @BeforeEach
    public void beforeEach() {
        var email = Generators.timeBasedGenerator().generate().toString() + "zdu.strong@gmail.com";
        this.user = this.createAccount(email);
        this.jwtId = Generators.timeBasedGenerator().generate().toString();
    }

}
