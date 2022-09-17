package com.springboot.project.test.service.FriendshipService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.KeyGenerator;

import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.fasterxml.uuid.Generators;
import com.springboot.project.model.TokenModel;
import com.springboot.project.test.BaseTest;
import cn.hutool.crypto.asymmetric.KeyType;

public class FriendshipServiceGetBlacklistTest extends BaseTest {
    private TokenModel user;
    private TokenModel friend;

    @Test
    public void test() throws NoSuchAlgorithmException, InvalidKeySpecException {
        var result = this.friendshipService.getBlackList(1, 10, this.user.getUserModel().getId());
        assertEquals(1, result.getTotalRecord());
        assertEquals(1, result.getList().size());
        assertTrue(JinqStream.from(result.getList()).select(s -> s.getIsInBlacklist()).getOnlyValue());
        assertFalse(JinqStream.from(result.getList()).select(s -> s.getIsFriend()).getOnlyValue());
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
        this.friendshipService.createFriendship(this.user.getUserModel().getId(), this.friend.getUserModel().getId(),
                aesOfUser, aesOfFriend);
        this.friendshipService.addToBlacklist(this.user.getUserModel().getId(), this.friend.getUserModel().getId());
    }

}
