package com.springboot.project.test.service.UserEmailService;

import java.net.URISyntaxException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.fasterxml.uuid.Generators;
import com.springboot.project.model.TokenModel;
import com.springboot.project.test.BaseTest;

public class UserEmailServiceUpdateUserEmailWithVerificationCodePassedTest extends BaseTest {
    private TokenModel tokenModel;
    private String verificationCode;
    private String email;

    @Test
    public void test() throws URISyntaxException {
        this.userEmailService.updateUserEmailWithVerificationCodePassed(this.email, this.tokenModel.getUserModel().getId(),
                verificationCode);
    }

    @BeforeEach
    public void beforeEach() {
        var email = Generators.timeBasedGenerator().generate().toString() + "zdu.strong@gmail.com";
        this.tokenModel = this.createAccount(email);
        this.verificationCode = "9527";
        this.email = Generators.timeBasedGenerator().generate().toString() + "zdu.strong@gmail.com";
        this.userEmailService.createUserEmailWithVerificationCode(this.email, this.tokenModel.getUserModel().getId(),
                this.verificationCode);
    }

}
