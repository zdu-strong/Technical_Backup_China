package com.springboot.project.service;

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
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.stereotype.Service;
import com.springboot.project.entity.EncryptDecryptEntity;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.crypto.symmetric.AES;

@Service
public class EncryptDecryptService extends BaseService {

    private RSAPublicKey keyOfRSAPublicKey;
    private RSAPrivateKey keyOfRSAPrivateKey;
    private SecretKey keyOfAESSecretKey;
    private final String keyId = "a6348051-646d-4f37-a68a-75a5a28a1d67";
    private Boolean ready = false;

    public String encryptByAES(String text) {
        var aes = this.getAES();
        return aes.encryptBase64(text);
    }

    public String decryptByAES(String text) {
        var aes = this.getAES();
        return aes.decryptStr(text);
    }

    public String encryptByPrivateKeyOfRSA(String text) {
        var rsa = this.getRSA();
        return rsa.encryptBase64(text, KeyType.PrivateKey);
    }

    public String decryptByByPublicKeyOfRSA(String text) {
        var rsa = this.getRSA();
        return rsa.decryptStr(text, KeyType.PublicKey);
    }

    public String decryptByByPrivateKeyOfRSA(String text) {
        var rsa = this.getRSA();
        return rsa.decryptStr(text, KeyType.PrivateKey);
    }

    public AES getAES() {
        this.generateKey();
        var aes = new AES(this.keyOfAESSecretKey);
        return aes;
    }

    public RSA getRSA() {
        this.generateKey();
        var rsa = new RSA(this.keyOfRSAPrivateKey, this.keyOfRSAPublicKey);
        return rsa;
    }

    private void generateKey() {
        try {
            if (!this.ready) {
                synchronized (getClass()) {
                    if (!this.ready) {
                        String id = this.keyId;
                        if (!this.EncryptDecryptEntity().where(s -> s.getId().equals(id)).exists()) {

                            EncryptDecryptEntity encryptDecryptEntity = new EncryptDecryptEntity();
                            encryptDecryptEntity.setCreateDate(new Date());
                            encryptDecryptEntity.setUpdateDate(new Date());

                            /**
                             * aes for common uses
                             */
                            encryptDecryptEntity.setId(this.keyId);
                            var keyGenerator = KeyGenerator.getInstance("AES");
                            keyGenerator.init(256);
                            encryptDecryptEntity.setSecretKeyOfAES(
                                    Base64.getEncoder().encodeToString(keyGenerator.generateKey().getEncoded()));

                            /**
                             * rsa for common uses
                             */
                            var keyPairGenerator = KeyPairGenerator.getInstance("RSA");
                            keyPairGenerator.initialize(2048);
                            var keyPair = keyPairGenerator.generateKeyPair();
                            encryptDecryptEntity.setPublicKeyOfRSA(
                                    Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()));
                            encryptDecryptEntity.setPrivateKeyOfRSA(
                                    Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded()));

                            this.entityManager.persist(encryptDecryptEntity);
                        }
                        EncryptDecryptEntity encryptDecryptEntity = this.EncryptDecryptEntity()
                                .where(s -> s.getId().equals(id)).getOnlyValue();
                        this.keyOfRSAPublicKey = (RSAPublicKey) KeyFactory.getInstance("RSA")
                                .generatePublic(new X509EncodedKeySpec(
                                        Base64.getDecoder().decode(encryptDecryptEntity.getPublicKeyOfRSA())));
                        this.keyOfRSAPrivateKey = (RSAPrivateKey) KeyFactory.getInstance("RSA")
                                .generatePrivate(new PKCS8EncodedKeySpec(
                                        Base64.getDecoder().decode(encryptDecryptEntity.getPrivateKeyOfRSA())));
                        this.keyOfAESSecretKey = new SecretKeySpec(
                                Base64.getDecoder().decode(encryptDecryptEntity.getSecretKeyOfAES()), "AES");
                        this.ready = true;
                    }
                }
            }
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
