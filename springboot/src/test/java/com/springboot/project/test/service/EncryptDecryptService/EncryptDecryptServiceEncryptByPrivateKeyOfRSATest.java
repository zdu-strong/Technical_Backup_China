package com.springboot.project.test.service.EncryptDecryptService;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import com.springboot.project.test.BaseTest;

public class EncryptDecryptServiceEncryptByPrivateKeyOfRSATest extends BaseTest {
    private String text = "Hello, world!";

    @Test
    public void test() {
        var result = this.encryptDecryptService.encryptByPrivateKeyOfRSA(text);
        assertEquals(text,
                this.encryptDecryptService.decryptByByPublicKeyOfRSA(result));
    }

}
