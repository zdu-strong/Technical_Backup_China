package com.springboot.project.test.service.EncryptDecryptService;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import com.springboot.project.test.BaseTest;

public class EncryptDecryptServiceGetRSATest extends BaseTest {

    @Test
    public void test() {
        assertNotNull(this.encryptDecryptService.getRSA());
    }

}
