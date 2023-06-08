package com.springboot.project.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.tika.Tika;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.devtools.remote.client.HttpHeaderInterceptor;
import org.springframework.boot.info.GitProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import com.beust.jcommander.internal.Lists;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.project.common.longtermtask.LongTermTaskUtil;
import com.springboot.project.common.permission.AuthorizationEmailUtil;
import com.springboot.project.common.permission.PermissionUtil;
import com.springboot.project.common.permission.TokenUtil;
import com.springboot.project.common.storage.ResourceHttpHeadersUtil;
import com.springboot.project.common.storage.Storage;
import com.springboot.project.model.TokenModel;
import com.springboot.project.model.UserEmailModel;
import com.springboot.project.model.UserModel;
import com.springboot.project.properties.AuthorizationEmailProperties;
import com.springboot.project.properties.StorageRootPathProperties;
import com.springboot.project.scheduled.StorageSpaceScheduled;
import com.springboot.project.service.EncryptDecryptService;
import com.springboot.project.service.FriendshipService;
import com.springboot.project.service.LoggerService;
import com.springboot.project.service.LongTermTaskService;
import com.springboot.project.service.OrganizeService;
import com.springboot.project.service.StorageSpaceService;
import com.springboot.project.service.TokenService;
import com.springboot.project.service.UserEmailService;
import com.springboot.project.service.UserMessageService;
import com.springboot.project.service.UserService;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.crypto.symmetric.AES;

/**
 * 
 * @author Me
 *
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BaseTest {

    @Autowired
    protected TestRestTemplate testRestTemplate;

    protected MockHttpServletRequest request = new MockHttpServletRequest();

    @Autowired
    protected StorageSpaceService storageSpaceService;

    @Autowired
    protected StorageSpaceScheduled storageSpaceScheduled;

    @Autowired
    protected Storage storage;

    @Autowired
    protected ResourceHttpHeadersUtil resourceHttpHeadersUtil;

    @Autowired
    protected StorageRootPathProperties storageRootPathProperties;

    @Autowired
    protected EncryptDecryptService encryptDecryptService;

    @Autowired
    protected UserService userService;

    @Autowired
    protected UserMessageService userMessageService;

    @Autowired
    protected LongTermTaskService longTermTaskService;

    @Autowired
    protected OrganizeService organizeService;

    @Autowired
    protected UserEmailService userEmailService;

    @Autowired
    protected TokenService tokenService;

    @Autowired
    protected FriendshipService friendshipService;

    @Autowired
    protected LoggerService loggerService;

    @Autowired
    protected PermissionUtil permissionUtil;

    @Autowired
    protected TokenUtil tokenUtil;

    @Autowired
    protected AuthorizationEmailProperties authorizationEmailProperties;

    @SpyBean
    protected AuthorizationEmailUtil authorizationEmailUtil;

    @Autowired
    protected LongTermTaskUtil longTermTaskUtil;

    @Autowired
    protected GitProperties gitProperties;

    @BeforeEach
    public void beforeEachOfBaseTest() {
        Mockito.doNothing().when(this.authorizationEmailUtil).sendVerificationCode(Mockito.anyString(),
                Mockito.anyString());
    }

    protected TokenModel createAccount(String email) {
        var password = email;
        try {
            if (!hasExistUser(email)) {
                UserModel userModelOfNewAccount = createNewAccount();
                signUp(userModelOfNewAccount, email, password);
            }
            return signIn(email, password);
        } catch (URISyntaxException | InvalidKeySpecException | NoSuchAlgorithmException | JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    protected MultipartFile createTempMultipartFile(Resource resource) {
        try {
            try (InputStream input = resource.getInputStream()) {
                Tika tika = new Tika();
                return new MockMultipartFile(this.storage.getFileNameFromResource(resource),
                        this.storage.getFileNameFromResource(resource),
                        tika.detect(this.storage.getFileNameFromResource(resource)), IOUtils.toByteArray(input));
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
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

    private void signUp(UserModel userModelOfNewAccount, String email, String password)
            throws InvalidKeySpecException, NoSuchAlgorithmException, URISyntaxException, JsonProcessingException {
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
                .setPrivateKeyOfRSA(new AES(this.encryptDecryptService.generateSecretKeyOfAES(new ObjectMapper()
                        .writeValueAsString(Lists.newArrayList(userModelOfNewAccount.getId(), password))))
                        .encryptBase64(Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded())));
        var url = new URIBuilder("/sign_up").build();
        var response = this.testRestTemplate.postForEntity(url, new HttpEntity<>(userModelOfSignUp),
                Object.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    private boolean hasExistUser(String email) {
        try {
            this.userService.getAccountForSignIn(email);
            return true;
        } catch (Throwable e) {
            return false;
        }
    }

    private UserModel getUserInfo(String accessToken) throws URISyntaxException {
        var url = new URIBuilder("/get_user_info").build();
        var response = this.testRestTemplate.getForEntity(url, UserModel.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        var user = response.getBody();
        return user;
    }

    private TokenModel signIn(String email, String password)
            throws URISyntaxException, InvalidKeySpecException, NoSuchAlgorithmException, JsonMappingException,
            JsonProcessingException {
        UserModel user;
        {
            var url = new URIBuilder("/sign_in/get_account").setParameter("userId", email).build();
            var response = this.testRestTemplate.postForEntity(url, null, UserModel.class);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            user = response.getBody();
        }
        {
            var privateKeyOfRSA = (RSAPrivateKey) KeyFactory.getInstance("RSA")
                    .generatePrivate(new PKCS8EncodedKeySpec(
                            Base64.getDecoder()
                                    .decode(new AES(this.encryptDecryptService.generateSecretKeyOfAES(new ObjectMapper()
                                            .writeValueAsString(Lists.newArrayList(user.getId(), password))))
                                            .decryptStr(user.getPrivateKeyOfRSA()))));
            var rsa = new RSA(privateKeyOfRSA, null);
            var passwordParameter = rsa.encryptBase64(new ObjectMapper().writeValueAsString(new Date()),
                    KeyType.PrivateKey);
            var url = new URIBuilder("/sign_in").setParameter("userId", user.getId())
                    .setParameter("password", passwordParameter)
                    .setParameter("privateKeyOfRSA", "privateKeyOfRSA")
                    .build();
            var response = this.testRestTemplate.postForEntity(url, null, String.class);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            var accessToken = response.getBody();
            this.testRestTemplate.getRestTemplate()
                    .setInterceptors(Lists.newArrayList(new HttpHeaderInterceptor(HttpHeaders.AUTHORIZATION,
                            "Bearer " + accessToken)));
            var tokenModel = new TokenModel();
            tokenModel.setUserModel(getUserInfo(accessToken));
            tokenModel.setAccess_token(accessToken);
            var publicKeyOfRSA = (RSAPublicKey) KeyFactory.getInstance("RSA")
                    .generatePublic(new X509EncodedKeySpec(
                            Base64.getDecoder().decode(tokenModel.getUserModel().getPublicKeyOfRSA())));
            tokenModel.setRSA(new RSA(rsa.getPrivateKey(), publicKeyOfRSA));
            return tokenModel;
        }
    }

}
