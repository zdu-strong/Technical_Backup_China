package com.springboot.project.test.controller.AuthorizationController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.net.URISyntaxException;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.uuid.Generators;
import com.google.common.collect.Lists;
import com.springboot.project.model.UserEmailModel;
import com.springboot.project.model.UserModel;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import com.springboot.project.test.BaseTest;

public class AuthorizationControllerSignUpTest extends BaseTest {
    private UserModel userModelOfNewAccount;

    @Test
    public void test()
            throws URISyntaxException, InvalidKeySpecException, NoSuchAlgorithmException, JsonProcessingException {
        var email = Generators.timeBasedGenerator().generate().toString() + "zdu.strong@gmail.com";
        String verificationCode = sendVerificationCode(email, userModelOfNewAccount.getId(),
                userModelOfNewAccount.getPublicKeyOfRSA());
        var publicKeyOfRSA = (RSAPublicKey) KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(
                        Base64.getDecoder().decode(userModelOfNewAccount.getPublicKeyOfRSA())));
        RSA rsa = new RSA(null, publicKeyOfRSA);
        var keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        var keyPair = keyPairGenerator.generateKeyPair();
        var userModelOfSignUp = new UserModel();
        userModelOfSignUp.setId(userModelOfNewAccount.getId()).setUsername(email)
                .setPassword(rsa.encryptBase64(userModelOfNewAccount.getId(), KeyType.PublicKey))
                .setUserEmailList(Lists.newArrayList(new UserEmailModel().setEmail(email)
                        .setVerificationCode(verificationCode)))
                .setPublicKeyOfRSA(Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()));
        userModelOfSignUp
                .setPrivateKeyOfRSA(this.encryptDecryptService
                        .encryptByAES(Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded())));
        var url = new URIBuilder("/sign_up").build();
        var response = this.testRestTemplate.postForEntity(url, new HttpEntity<>(userModelOfSignUp),
                Object.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @BeforeEach
    public void beforeEach() {
        this.userModelOfNewAccount = createNewAccount();
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

    private String sendVerificationCode(String email, String userId, String publicKeyOfRSAString)
            throws URISyntaxException, InvalidKeySpecException, NoSuchAlgorithmException {
        var publicKeyOfRSA = (RSAPublicKey) KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(
                        Base64.getDecoder().decode(publicKeyOfRSAString)));
        RSA rsa = new RSA(null, publicKeyOfRSA);
        var userEmailModel = new UserEmailModel().setEmail(email)
                .setVerificationCode(rsa.encryptBase64(userId, KeyType.PublicKey));
        var userModel = new UserModel();
        userModel.setId(userId);
        userModel.setUserEmailList(Lists.newArrayList(userEmailModel));
        Mockito.doAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                var verificationCode = String.valueOf(args[1]);
                userEmailModel.setVerificationCode(verificationCode);
                return null;
            }
        }).when(this.authorizationEmailUtil).sendVerificationCode(Mockito.anyString(), Mockito.anyString());
        var url = new URIBuilder("/sign_up/send_verification_code").build();
        var response = this.testRestTemplate.postForEntity(url, new HttpEntity<>(userModel), Object.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        return userEmailModel.getVerificationCode();
    }
}
