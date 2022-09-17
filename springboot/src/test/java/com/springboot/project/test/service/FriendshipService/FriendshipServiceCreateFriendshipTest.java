package com.springboot.project.test.service.FriendshipService;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.KeyGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.fasterxml.uuid.Generators;
import com.springboot.project.model.UserModel;
import com.springboot.project.test.BaseTest;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;

public class FriendshipServiceCreateFriendshipTest extends BaseTest {
    private UserModel user;
    private UserModel friend;

    @Test
    public void test() throws NoSuchAlgorithmException, InvalidKeySpecException {
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
    }

    @BeforeEach
    public void beforeEach() {
        var userEmail = Generators.timeBasedGenerator().generate().toString() + "zdu.strong@gmail.com";
        var friendEmail = Generators.timeBasedGenerator().generate().toString() + "zdu.strong@gmail.com";
        this.user = this.createAccount(userEmail).getUserModel();
        this.friend = this.createAccount(friendEmail).getUserModel();
    }

}
