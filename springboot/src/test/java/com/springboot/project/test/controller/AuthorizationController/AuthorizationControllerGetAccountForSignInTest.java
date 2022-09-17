package com.springboot.project.test.controller.AuthorizationController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import com.fasterxml.uuid.Generators;
import com.springboot.project.model.TokenModel;
import com.springboot.project.model.UserModel;
import com.springboot.project.test.BaseTest;

public class AuthorizationControllerGetAccountForSignInTest extends BaseTest {
    private TokenModel tokenModel;
    private String email;

    @Test
    public void test() throws URISyntaxException, InvalidKeySpecException, NoSuchAlgorithmException {
        var url = new URIBuilder("/sign_in/get_account").setParameter("userId", email).build();
        var response = this.testRestTemplate.postForEntity(url, null, UserModel.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(this.tokenModel.getUserModel().getId(), response.getBody().getId());
        assertTrue(StringUtils.isNotBlank(response.getBody().getPrivateKeyOfRSA()));
        assertTrue(response.getBody().getHasRegistered());
        assertNotNull(response.getBody().getUserSignInVerificationCode());
        assertTrue(StringUtils.isNotBlank(response.getBody().getUserSignInVerificationCode().getId()));
        assertTrue(StringUtils.isNotBlank(response.getBody().getUserSignInVerificationCode().getVerificationCode()));
        assertEquals(5, response.getBody().getUserSignInVerificationCode().getVerificationCode().length());
        assertNotNull(response.getBody().getUserSignInVerificationCode().getCreateDate());
        assertNotNull(response.getBody().getUserSignInVerificationCode().getUpdateDate());
        assertTrue(StringUtils.isNotBlank(response.getBody().getUserSignInVerificationCode().getUser().getId()));
        assertTrue(StringUtils.isBlank(response.getBody().getUsername()));
        assertTrue(StringUtils.isBlank(response.getBody().getEmail()));
        assertTrue(StringUtils.isBlank(response.getBody().getPassword()));
        assertTrue(StringUtils.isBlank(response.getBody().getPublicKeyOfRSA()));
        assertNull(response.getBody().getUserEmailList());
    }

    @BeforeEach
    public void beforeEach() throws InvalidKeySpecException, NoSuchAlgorithmException, URISyntaxException {
        this.email = Generators.timeBasedGenerator().generate().toString() + "zdu.strong@gmail.com";
        this.tokenModel = this.createAccount(email);
    }

}
