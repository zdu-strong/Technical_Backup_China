package com.springboot.project.test.service.UserSignInVerificationCodeService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.net.URISyntaxException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import com.fasterxml.uuid.Generators;
import com.google.common.collect.Lists;
import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import com.springboot.project.model.UserEmailModel;
import com.springboot.project.model.UserModel;
import com.springboot.project.test.BaseTest;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;

public class UserSignInVerificationCodeServiceCreateVerificationCodeForSignInTest extends BaseTest {
    private UserModel userModel;
    private String email;

    @Test
    public void test() throws URISyntaxException, InvalidKeySpecException, NoSuchAlgorithmException {
        var publicKeyOfRSA = (RSAPublicKey) KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(
                        Base64.getDecoder().decode(this.userModel.getPublicKeyOfRSA())));
        RSA rsa = new RSA(null, publicKeyOfRSA);
        var userEmailModel = new UserEmailModel().setEmail(this.email)
                .setVerificationCode(rsa.encryptBase64(this.userModel.getId(), KeyType.PublicKey));
        var userModel = new UserModel();
        userModel.setId(this.userModel.getId());
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
    }

    @BeforeEach
    public void beforeEach() {
        this.email = Generators.timeBasedGenerator().generate().toString() + "zdu.strong@gmail.com";
        this.userModel = getNewAccount(email);
    }

    private UserModel getNewAccount(String email) {
        try {
            var url = new URIBuilder("/sign_up/get_new_account").build();
            var response = this.testRestTemplate.postForEntity(url, null, UserModel.class);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            var userModel = response.getBody();
            return userModel;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
