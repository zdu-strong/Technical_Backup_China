package com.springboot.project.test.service.EncryptDecryptService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.springboot.project.test.BaseTest;

public class EncryptDecryptServiceDecryptByByPrivateKeyOfRSATest extends BaseTest {
    private String text = "Hello, world!";
    private String textOfEncryptByPublicKeyOfRSA;

    @Test
    public void test() {
        assertEquals(text,
                this.encryptDecryptService.decryptByByPrivateKeyOfRSA(this.textOfEncryptByPublicKeyOfRSA));
    }

    @BeforeEach
    public void beforeEach() {
        this.textOfEncryptByPublicKeyOfRSA = this.encryptDecryptService.encryptByPublicKeyOfRSA(text);
    }

}
