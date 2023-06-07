package com.springboot.project.test.service.FriendshipService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.KeyGenerator;
import org.apache.commons.lang3.StringUtils;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.fasterxml.uuid.Generators;
import com.springboot.project.model.TokenModel;
import com.springboot.project.test.BaseTest;
import cn.hutool.crypto.asymmetric.KeyType;

public class FriendshipServiceGetFriendListTest extends BaseTest {
    private TokenModel user;
    private TokenModel friend;

    @Test
    public void test() throws NoSuchAlgorithmException, InvalidKeySpecException {
        var result = this.friendshipService.getFriendList(1L, 10L, this.user.getUserModel().getId());
        assertEquals(1, result.getTotalRecord());
        assertEquals(user.getUserModel().getId(),
                JinqStream.from(result.getList()).select(s -> s.getUser().getId()).getOnlyValue());
        assertEquals(friend.getUserModel().getId(),
                JinqStream.from(result.getList()).select(s -> s.getFriend().getId()).getOnlyValue());
        assertTrue(JinqStream.from(result.getList()).select(s -> s.getIsFriend()).getOnlyValue());
        assertFalse(JinqStream.from(result.getList()).select(s -> s.getIsInBlacklist()).getOnlyValue());
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
        assertFalse(JinqStream.from(result.getList()).select(s -> s.getIsInBlacklist()).getOnlyValue());
    }

    @BeforeEach
    public void beforeEach() throws NoSuchAlgorithmException, InvalidKeySpecException {
        var userEmail = Generators.timeBasedGenerator().generate().toString() + "zdu.strong@gmail.com";
        var friendEmail = Generators.timeBasedGenerator().generate().toString() + "zdu.strong@gmail.com";
        this.user = this.createAccount(userEmail);
        this.friend = this.createAccount(friendEmail);
        var keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        var keyOfAES = Base64.getEncoder().encodeToString(keyGenerator.generateKey().getEncoded());
        var aesOfUser = this.user.getRSA().encryptBase64(this.user.getRSA().encryptBase64(keyOfAES, KeyType.PrivateKey),
                KeyType.PublicKey);
        var aesOfFriend = this.friend.getRSA()
                .encryptBase64(this.user.getRSA().encryptBase64(keyOfAES, KeyType.PrivateKey), KeyType.PublicKey);
        this.friendshipService.createFriendship(this.user.getUserModel().getId(),
                this.friend.getUserModel().getId(), aesOfUser, aesOfFriend);
        this.friendshipService.addFriend(this.user.getUserModel().getId(), this.friend.getUserModel().getId());
    }

}
