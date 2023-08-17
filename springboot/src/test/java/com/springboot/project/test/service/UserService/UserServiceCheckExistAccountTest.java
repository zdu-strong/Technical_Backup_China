package com.springboot.project.test.service.UserService;

import java.net.URISyntaxException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.fasterxml.uuid.Generators;
import com.springboot.project.test.BaseTest;

public class UserServiceCheckExistAccountTest extends BaseTest {
    private String email;

    @Test
    public void test() throws URISyntaxException {
        this.userService.checkExistAccount(this.email);
    }

    @BeforeEach
    public void beforeEach() {
        this.email = Generators.timeBasedGenerator().generate().toString() + "zdu.strong@gmail.com";
        this.createAccount(email);
    }

}
