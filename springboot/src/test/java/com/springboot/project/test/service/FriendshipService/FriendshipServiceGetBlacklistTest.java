package com.springboot.project.test.service.FriendshipService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.fasterxml.uuid.Generators;
import com.springboot.project.model.UserModel;
import com.springboot.project.test.BaseTest;

public class FriendshipServiceGetBlacklistTest extends BaseTest {
    private UserModel user;
    private UserModel friend;

    @Test
    public void test() throws NoSuchAlgorithmException, InvalidKeySpecException {
        var result = this.friendshipService.getBlackList(1L, 10L, this.user.getId());
        assertEquals(1, result.getTotalRecord());
        assertEquals(1, result.getList().size());
        assertTrue(JinqStream.from(result.getList()).select(s -> s.getIsInBlacklist()).getOnlyValue());
        assertFalse(JinqStream.from(result.getList()).select(s -> s.getIsFriend()).getOnlyValue());
        assertEquals(this.friend.getId(),
                JinqStream.from(result.getList()).select(s -> s.getFriend().getId()).getOnlyValue());
    }

    @BeforeEach
    public void beforeEach() throws NoSuchAlgorithmException, InvalidKeySpecException {
        var userEmail = Generators.timeBasedGenerator().generate().toString() + "zdu.strong@gmail.com";
        var friendEmail = Generators.timeBasedGenerator().generate().toString() + "zdu.strong@gmail.com";
        this.user = this.createAccount(userEmail);
        this.friend = this.createAccount(friendEmail);
        var keyOfAES = this.encryptDecryptService.generateSecretKeyOfAES();
        var aesOfUser = this.encryptDecryptService.encryptByPublicKeyOfRSA(
                this.encryptDecryptService.encryptByPrivateKeyOfRSA(keyOfAES, this.user.getPrivateKeyOfRSA()),
                this.user.getPublicKeyOfRSA());
        var aesOfFriend = this.encryptDecryptService.encryptByPublicKeyOfRSA(
                this.encryptDecryptService.encryptByPrivateKeyOfRSA(keyOfAES, this.user.getPrivateKeyOfRSA()),
                this.friend.getPublicKeyOfRSA());
        this.friendshipService.createFriendship(this.user.getId(), this.friend.getId(), aesOfUser, aesOfFriend);
        this.friendshipService.addToBlacklist(this.user.getId(), this.friend.getId());
    }

}
