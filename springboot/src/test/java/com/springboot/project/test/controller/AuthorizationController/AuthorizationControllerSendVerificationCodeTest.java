package com.springboot.project.test.controller.AuthorizationController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.net.URISyntaxException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import com.beust.jcommander.internal.Lists;
import com.fasterxml.uuid.Generators;
import com.springboot.project.model.UserEmailModel;
import com.springboot.project.model.UserModel;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import com.springboot.project.test.BaseTest;

public class AuthorizationControllerSendVerificationCodeTest extends BaseTest {
    private UserModel userModelOfNewAccount;
    private String email;

    @Test
    public void test() throws URISyntaxException, InvalidKeySpecException, NoSuchAlgorithmException {
        var publicKeyOfRSA = (RSAPublicKey) KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(
                        Base64.getDecoder().decode(this.userModelOfNewAccount.getPublicKeyOfRSA())));
        RSA rsa = new RSA(null, publicKeyOfRSA);
        var userEmailModel = new UserEmailModel().setEmail(email)
                .setVerificationCode(rsa.encryptBase64(this.userModelOfNewAccount.getId(), KeyType.PublicKey));
        var userModel = new UserModel();
        userModel.setId(this.userModelOfNewAccount.getId());
        userModel.setUserEmailList(Lists.newArrayList(userEmailModel));
        var url = new URIBuilder("/sign_up/send_verification_code").build();
        var response = this.testRestTemplate.postForEntity(url, new HttpEntity<>(userModel), Object.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @BeforeEach
    public void beforeEach() {
        this.userModelOfNewAccount = createNewAccount();
        this.email = Generators.timeBasedGenerator().generate().toString() + "zdu.strong@gmail.com";
    }

    private UserModel createNewAccount() {
        try {
            var url = new URIBuilder("/sign_up/create_new_account").build();
            var response = this.testRestTemplate.postForEntity(url, null, UserModel.class);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            var userModel = response.getBody();
            return userModel;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
