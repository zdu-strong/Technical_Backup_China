package com.springboot.project.test.service.VerificationCodeEmailService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.net.URISyntaxException;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.fasterxml.uuid.Generators;
import com.springboot.project.model.VerificationCodeEmailModel;
import com.springboot.project.test.BaseTest;

public class VerificationCodeEmailServiceIsFirstOnTheDurationOfVerificationCodeEmailTest extends BaseTest {
    private VerificationCodeEmailModel verificationCodeEmailModel;

    @Test
    public void test() throws URISyntaxException {
        var result = this.verificationCodeEmailService
                .isFirstOnTheDurationOfVerificationCodeEmail(this.verificationCodeEmailModel.getId());
        assertTrue(result);
    }

    @BeforeEach
    public void beforeEach() {
        var email = Generators.timeBasedGenerator().generate().toString() + "zdu.strong@gmail.com";
        var result = this.verificationCodeEmailService.createVerificationCodeEmail(email);
        assertTrue(StringUtils.isNotBlank(result.getId()));
        assertEquals(email, result.getEmail());
        assertTrue(StringUtils.isNotBlank(result.getVerificationCode()));
        assertEquals(6, result.getVerificationCode().length());
        assertTrue(Pattern.compile("^[0-9]{6}$").matcher(result.getVerificationCode()).find());
        assertEquals(6, result.getVerificationCodeLength());
        assertFalse(result.getHasUsed());
        assertFalse(result.getIsPassed());
        assertNotNull(result.getCreateDate());
        assertNotNull(result.getUpdateDate());
        this.verificationCodeEmailModel = result;
    }

}
