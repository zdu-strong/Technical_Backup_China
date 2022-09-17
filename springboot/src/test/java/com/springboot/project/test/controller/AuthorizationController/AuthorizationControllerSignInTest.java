package com.springboot.project.test.controller.AuthorizationController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.net.URISyntaxException;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import com.alibaba.fastjson.JSON;
import com.fasterxml.uuid.Generators;
import com.springboot.project.model.UserModel;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import com.springboot.project.test.BaseTest;

public class AuthorizationControllerSignInTest extends BaseTest {
    private RSA rsa;
    private UserModel user;

    @Test
    public void test() throws URISyntaxException, InvalidKeySpecException, NoSuchAlgorithmException {
        var userModel = new UserModel();
        userModel.setId(user.getId());
        userModel.setPassword(JSON
                .parseObject(this.encryptDecryptService
                        .decryptByAES(user.getPrivateKeyOfRSA()))
                .getString("password"));
        userModel.setUserSignInVerificationCode(user.getUserSignInVerificationCode());
        var keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        var keyPair = keyPairGenerator.generateKeyPair();
        var newRSA = new RSA(null, keyPair.getPublic());
        userModel.setPrivateKeyOfRSA(newRSA.encryptBase64(this.rsa.getPrivateKeyBase64(), KeyType.PublicKey));
        var password = rsa.encryptBase64(JSON.toJSONString(userModel), KeyType.PrivateKey);
        var url = new URIBuilder("/sign_in").setParameter("userId", user.getId()).setParameter("password", password)
                .build();
        var response = this.testRestTemplate.postForEntity(url, null, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(StringUtils.isNotBlank(response.getBody()));
    }

    @BeforeEach
    public void beforeEach() throws InvalidKeySpecException, NoSuchAlgorithmException, URISyntaxException {
        var email = Generators.timeBasedGenerator().generate().toString() + "zdu.strong@gmail.com";
        var tokenModel = this.createAccount(email);
        this.rsa = tokenModel.getRSA();
        this.user = getAccount(email);
    }

    private UserModel getAccount(String email) throws URISyntaxException {
        var url = new URIBuilder("/sign_in/get_account").setParameter("userId", email).build();
        var response = this.testRestTemplate.postForEntity(url, null, UserModel.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        var user = response.getBody();
        return user;
    }

}
