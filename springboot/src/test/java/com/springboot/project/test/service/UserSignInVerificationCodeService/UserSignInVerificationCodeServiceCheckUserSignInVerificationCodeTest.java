package com.springboot.project.test.service.UserSignInVerificationCodeService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import com.fasterxml.uuid.Generators;
import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import com.springboot.project.model.UserModel;
import com.springboot.project.test.BaseTest;

public class UserSignInVerificationCodeServiceCheckUserSignInVerificationCodeTest extends BaseTest {
    private UserModel userModel;

    @Test
    public void test() throws URISyntaxException, InvalidKeySpecException, NoSuchAlgorithmException {
        this.userSignInVerificationCodeService.checkUserSignInVerificationCode(this.userModel.getId(), this.userModel.getUserSignInVerificationCode());
    }

    @BeforeEach
    public void beforeEach() throws InvalidKeySpecException, NoSuchAlgorithmException, URISyntaxException {
        var email = Generators.timeBasedGenerator().generate().toString() + "zdu.strong@gmail.com";
        this.createAccount(email);
        var url = new URIBuilder("/sign_in/get_account").setParameter("userId", email).build();
        var response = this.testRestTemplate.postForEntity(url, null, UserModel.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        this.userModel = response.getBody();
    }

}
