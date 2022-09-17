package com.springboot.project.test.service.FriendshipService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.KeyGenerator;
import org.apache.commons.lang3.StringUtils;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.fasterxml.uuid.Generators;
import com.springboot.project.model.UserModel;
import com.springboot.project.test.BaseTest;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;

public class FriendshipServiceGetFriendListTest extends BaseTest {
    private UserModel user;
    private UserModel friend;

    @Test
    public void test() throws NoSuchAlgorithmException, InvalidKeySpecException {
        var result = this.friendshipService.getFriendList(1, 10, this.user.getId());
        assertEquals(1, result.getTotalRecord());
        assertEquals(user.getId(), JinqStream.from(result.getList()).select(s -> s.getUser().getId()).getOnlyValue());
        assertEquals(friend.getId(),
                JinqStream.from(result.getList()).select(s -> s.getFriend().getId()).getOnlyValue());
        assertTrue(JinqStream.from(result.getList()).select(s -> s.getIsFriend()).getOnlyValue());
        assertFalse(JinqStream.from(result.getList()).select(s -> s.getIsBlacklist()).getOnlyValue());
        assertNotNull(JinqStream.from(result.getList()).select(s -> s.getCreateDate()).getOnlyValue());
        assertNotNull(JinqStream.from(result.getList()).select(s -> s.getUpdateDate()).getOnlyValue());
        assertTrue(StringUtils
                .isNotBlank(JinqStream.from(result.getList()).select(s -> s.getFriend().getUsername()).getOnlyValue()));
        assertTrue(StringUtils.isNotBlank(
                JinqStream.from(result.getList()).select(s -> s.getFriend().getPublicKeyOfRSA()).getOnlyValue()));
        assertTrue(StringUtils
                .isBlank(JinqStream.from(result.getList()).select(s -> s.getFriend().getPassword()).getOnlyValue()));
        assertTrue(StringUtils.isBlank(
                JinqStream.from(result.getList()).select(s -> s.getFriend().getPrivateKeyOfRSA()).getOnlyValue()));
        assertTrue(JinqStream.from(result.getList()).select(s -> s.getFriend().getHasRegistered()).getOnlyValue());
        assertTrue(JinqStream.from(result.getList()).select(s -> s.getIsFriend()).getOnlyValue());
        assertFalse(JinqStream.from(result.getList()).select(s -> s.getIsBlacklist()).getOnlyValue());
    }

    @BeforeEach
    public void beforeEach() throws NoSuchAlgorithmException, InvalidKeySpecException {
        var userEmail = Generators.timeBasedGenerator().generate().toString() + "zdu.strong@gmail.com";
        var friendEmail = Generators.timeBasedGenerator().generate().toString() + "zdu.strong@gmail.com";
        this.user = this.createAccount(userEmail).getUserModel();
        this.friend = this.createAccount(friendEmail).getUserModel();
        var keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        var keyOfAES = Base64.getEncoder().encodeToString(keyGenerator.generateKey().getEncoded());
        var aesOfUser = new RSA(null, (RSAPublicKey) KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(
                        Base64.getDecoder().decode(this.user.getPublicKeyOfRSA()))))
                .encryptBase64(keyOfAES, KeyType.PublicKey);
        var aesOfFriend = new RSA(null, (RSAPublicKey) KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(
                        Base64.getDecoder().decode(this.friend.getPublicKeyOfRSA()))))
                .encryptBase64(keyOfAES, KeyType.PublicKey);
        this.friendshipService.createFriendship(this.user.getId(), this.friend.getId(), aesOfUser, aesOfFriend);
        this.friendshipService.addFriend(this.user.getId(), this.friend.getId());
    }

}
