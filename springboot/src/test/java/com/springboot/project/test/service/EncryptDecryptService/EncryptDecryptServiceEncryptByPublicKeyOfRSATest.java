package com.springboot.project.test.service.EncryptDecryptService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import com.springboot.project.test.BaseTest;

public class EncryptDecryptServiceEncryptByPublicKeyOfRSATest extends BaseTest {
    private String text = "Hello, world!";

    @Test
    public void test() {
        var result = this.encryptDecryptService.encryptByPublicKeyOfRSA(text);
        assertEquals(text,
                this.encryptDecryptService.decryptByByPrivateKeyOfRSA(result));
    }

}
