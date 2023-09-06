package com.springboot.project.test.controller.AuthorizationController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import org.apache.http.client.utils.URIBuilder;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.uuid.Generators;
import com.google.common.collect.Lists;
import com.springboot.project.model.UserEmailModel;
import com.springboot.project.model.UserModel;
import com.springboot.project.model.VerificationCodeEmailModel;
import com.springboot.project.test.BaseTest;

public class AuthorizationControllerSignUpTest extends BaseTest {

    @Test
    public void test()
            throws URISyntaxException, InvalidKeySpecException, NoSuchAlgorithmException, JsonProcessingException {
        var email = Generators.timeBasedGenerator().generate().toString() + "zdu.strong@gmail.com";
        var verificationCodeEmail = sendVerificationCode(email);
        var keyPairOfRSA = this.encryptDecryptService.generateKeyPairOfRSA();
        var userModelOfSignUp = new UserModel();
        userModelOfSignUp.setUsername(email)
                .setUserEmailList(Lists.newArrayList(new UserEmailModel().setEmail(email)
                        .setVerificationCodeEmail(verificationCodeEmail)))
                .setPublicKeyOfRSA(keyPairOfRSA.getPublicKeyOfRSA());
        userModelOfSignUp
                .setPrivateKeyOfRSA(this.encryptDecryptService
                        .encryptByAES(keyPairOfRSA.getPrivateKeyOfRSA()));
        var keyPairOfRSAForPassword = this.encryptDecryptService.generateKeyPairOfRSA();
        userModelOfSignUp.setPassword(
                new ObjectMapper().writeValueAsString(Lists.newArrayList(
                        this.encryptDecryptService.encryptByAES(keyPairOfRSAForPassword.getPrivateKeyOfRSA()),
                        keyPairOfRSAForPassword.getPublicKeyOfRSA())));
        var url = new URIBuilder("/sign_up").build();
        var response = this.testRestTemplate.postForEntity(url, new HttpEntity<>(userModelOfSignUp),
                Object.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    private VerificationCodeEmailModel sendVerificationCode(String email) throws URISyntaxException {
        List<String> verificationCodeList = Lists.newArrayList();
        Mockito.doAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                var verificationCode = String.valueOf(args[1]);
                verificationCodeList.add(verificationCode);
                return null;
            }
        }).when(this.authorizationEmailUtil).sendVerificationCode(Mockito.anyString(), Mockito.anyString());
        var url = new URIBuilder("/email/send_verification_code").setParameter("email", email).build();
        var response = this.testRestTemplate.postForEntity(url, new HttpEntity<>(null),
                VerificationCodeEmailModel.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        response.getBody().setVerificationCode(JinqStream.from(verificationCodeList).getOnlyValue());
        return response.getBody();
    }

}
