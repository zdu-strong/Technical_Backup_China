package com.springboot.project.service;

import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import com.fasterxml.uuid.Generators;
import com.google.common.collect.Lists;

import org.jinq.orm.stream.JinqStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.springboot.project.entity.UserEntity;
import com.springboot.project.model.UserModel;

@Service
public class UserService extends BaseService {

    @Autowired
    private UserEmailService userEmailService;

    public UserModel createNewAccountForSignUp() {
        var user = new UserEntity();
        user.setId(Generators.timeBasedGenerator().generate().toString());
        user.setUsername("");
        user.setCreateDate(new Date());
        user.setUpdateDate(new Date());
        user.setUserEmailList(Lists.newArrayList());
        user.setUserMessageList(Lists.newArrayList());
        user.setFridendList(Lists.newArrayList());
        user.setReverseFridendList(Lists.newArrayList());
        user.setTokenList(Lists.newArrayList());
        user.setHasRegistered(false);
        user.setIsDeleted(false);
        try {
            var keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            var keyPair = keyPairGenerator.generateKeyPair();
            user.setPublicKeyOfRSA(
                    Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()));
            user.setPrivateKeyOfRSA(
                    Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded()));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        this.entityManager.persist(user);

        return this.userFormatter.format(user);
    }

    public void signUp(UserModel userModel) {
        var userId = userModel.getId();
        var userEntity = this.UserEntity().where(s -> s.getId().equals(userId)).where(s -> !s.getIsDeleted())
                .where(s -> !s.getHasRegistered())
                .getOnlyValue();
        userEntity.setCreateDate(new Date());
        userEntity.setUpdateDate(new Date());
        userEntity.setUsername(userModel.getUsername());
        userEntity.setIsDeleted(false);
        userEntity.setPrivateKeyOfRSA(userModel.getPrivateKeyOfRSA());
        userEntity.setPublicKeyOfRSA(userModel.getPublicKeyOfRSA());
        userEntity.setHasRegistered(true);
        this.entityManager.merge(userEntity);

        for (var userEmail : userModel.getUserEmailList()) {
            this.userEmailService.updateUserEmailWithVerificationCodePassed(userEmail.getEmail(), userEntity.getId(),
                    userEmail.getVerificationCode());
        }
    }

    public UserModel getAccountForSignIn(String userIdOrEmail) {
        var user = this.UserEntity()
                .where(s -> !s.getIsDeleted())
                .where(s -> s.getId().equals(userIdOrEmail) || JinqStream.from(s.getUserEmailList())
                        .where(m -> !m.getIsDeleted())
                        .where(m -> m.getEmail().equals(userIdOrEmail))
                        .exists())
                .getOnlyValue();
        return this.userFormatter.formatWithMoreInformation(user);
    }

    public UserModel getUserById(String userId) {
        var user = this.UserEntity().where(s -> s.getId().equals(userId)).where(s -> !s.getIsDeleted())
                .where(s -> s.getHasRegistered())
                .getOnlyValue();
        return this.userFormatter.format(user);
    }

    public void checkExistAccount(String userIdOrEmail) {
        var userId = userIdOrEmail;
        var email = userIdOrEmail;
        var stream = this.UserEntity()
                .where(s -> !s.getIsDeleted()).where(s -> s.getHasRegistered())
                .where(s -> s.getId().equals(userId) || JinqStream.from(s.getUserEmailList())
                        .where(m -> !m.getIsDeleted())
                        .where(m -> m.getEmail().equals(email))
                        .exists());
        if (!stream.exists()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Account does not exist");
        }
    }

}
