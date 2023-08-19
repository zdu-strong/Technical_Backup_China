package com.springboot.project.test.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import org.apache.http.client.utils.URIBuilder;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.BeforeEach;
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

public class UserServiceSignUpTest extends BaseTest {
    private String email;

    @Test
    public void test() throws URISyntaxException, InvalidKeySpecException, NoSuchAlgorithmException {
        try {
            var verificationCodeEmail = sendVerificationCode(email);
            var keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            var keyPair = keyPairGenerator.generateKeyPair();
            var realPassword = Base64.getEncoder().encodeToString(
                    Generators.timeBasedGenerator().generate().toString().getBytes(StandardCharsets.UTF_8));
            var userModelOfSignUp = new UserModel();
            userModelOfSignUp.setUsername(email)
                    .setUserEmailList(Lists.newArrayList(new UserEmailModel().setEmail(email)
                            .setVerificationCodeEmail(verificationCodeEmail)))
                    .setPublicKeyOfRSA(Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()));
            var map = new HashMap<>();
            map.put("password", realPassword);
            map.put("privateKeyOfRSA", Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded()));
            userModelOfSignUp
                    .setPrivateKeyOfRSA(this.encryptDecryptService
                            .encryptByAES(new ObjectMapper().writeValueAsString(map)));
            this.userService.signUp(userModelOfSignUp);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @BeforeEach
    public void beforeEach() {
        this.email = Generators.timeBasedGenerator().generate().toString() + "zdu.strong@gmail.com";
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