package com.springboot.project.test.service.UserEmailService;

import static org.junit.jupiter.api.Assertions.assertThrows;
import java.net.URISyntaxException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import com.fasterxml.uuid.Generators;
import com.springboot.project.model.TokenModel;
import com.springboot.project.test.BaseTest;

public class UserEmailServiceCreateUserEmailAddSameEamilTest extends BaseTest {
    private TokenModel tokenModel;
    private String email;

    @Test
    public void test() throws URISyntaxException {
        assertThrows(DataIntegrityViolationException.class, () -> {
            this.userEmailService.createUserEmail(this.email, this.tokenModel.getUserModel().getId());
        });
    }

    @BeforeEach
    public void beforeEach() {
        this.email = Generators.timeBasedGenerator().generate().toString() + "zdu.strong@gmail.com";
        this.tokenModel = this.createAccount(email);
    }

}
