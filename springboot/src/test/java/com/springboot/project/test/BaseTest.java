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
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.tika.Tika;
import org.jinq.orm.stream.JinqStream;
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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.springboot.project.common.TimeZoneUtil.TimeZoneUtils;
import com.springboot.project.common.longtermtask.LongTermTaskUtil;
import com.springboot.project.common.permission.AuthorizationEmailUtil;
import com.springboot.project.common.permission.PermissionUtil;
import com.springboot.project.common.permission.TokenUtil;
import com.springboot.project.common.storage.ResourceHttpHeadersUtil;
import com.springboot.project.common.storage.Storage;
import com.springboot.project.model.LongTermTaskModel;
import com.springboot.project.model.TokenModel;
import com.springboot.project.model.UserEmailModel;
import com.springboot.project.model.UserModel;
import com.springboot.project.model.VerificationCodeEmailModel;
import com.springboot.project.properties.AuthorizationEmailProperties;
import com.springboot.project.properties.StorageRootPathProperties;
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
import com.springboot.project.service.VerificationCodeEmailService;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.crypto.symmetric.AES;
import io.reactivex.rxjava3.core.Observable;

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
    protected Storage storage;

    @Autowired
    protected ResourceHttpHeadersUtil resourceHttpHeadersUtil;

    @Autowired
    protected TimeZoneUtils timeZoneUtils;

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
    protected VerificationCodeEmailService verificationCodeEmailService;

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
                signUp(email, password);
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

    private void signUp(String email, String password)
            throws InvalidKeySpecException, NoSuchAlgorithmException, URISyntaxException, JsonProcessingException {
        var verificationCodeEmail = sendVerificationCode(email);
        var keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        var keyPair = keyPairGenerator.generateKeyPair();
        var userModelOfSignUp = new UserModel();
        userModelOfSignUp.setUsername(email)
                .setUserEmailList(Lists.newArrayList(new UserEmailModel().setEmail(email)
                        .setVerificationCodeEmail(verificationCodeEmail)))
                .setPublicKeyOfRSA(Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()));
        userModelOfSignUp
                .setPrivateKeyOfRSA(new AES(this.encryptDecryptService.generateSecretKeyOfAES(password))
                        .encryptBase64(Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded())));
        var url = new URIBuilder("/sign_up").build();
        var response = this.testRestTemplate.postForEntity(url, new HttpEntity<>(userModelOfSignUp),
                UserModel.class);
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
                                    .decode(new AES(this.encryptDecryptService.generateSecretKeyOfAES(password))
                                            .decryptStr(user.getPrivateKeyOfRSA()))));
            var rsa = new RSA(privateKeyOfRSA, null);
            var passwordParameter = rsa.encryptBase64(
                    new ObjectMapper().writeValueAsString(
                            new UserModel().setCreateDate(new Date()).setPrivateKeyOfRSA("Private Key")),
                    KeyType.PrivateKey);
            var url = new URIBuilder("/sign_in").setParameter("userId", user.getId())
                    .setParameter("password", passwordParameter)
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

    protected String fromLongTermTask(Supplier<String> supplier) {
        var relativeUrlList = new ArrayList<String>();
        Observable.fromSupplier(() -> supplier.get()).concatMap((relativeUrl) -> {
            relativeUrlList.add(relativeUrl);
            while (true) {
                var url = new URIBuilder(this.testRestTemplate.getRootUri() + relativeUrl).build();
                var result = new RestTemplate().exchange(url, HttpMethod.GET, new HttpEntity<>(null),
                        new ParameterizedTypeReference<LongTermTaskModel<Object>>() {
                        });
                if (result.getBody().getIsDone()) {
                    break;
                }
                Thread.sleep(1000);
            }
            return Observable.empty();
        }).retry(s -> {
            if (s.getMessage().contains("The task failed because it stopped")) {
                return true;
            } else {
                return false;
            }
        }).onErrorComplete().blockingSubscribe();
        var relativeUrl = JinqStream.from(CollectionUtil.reverseNew(relativeUrlList)).findFirst().get();
        return relativeUrl;
    }

}
