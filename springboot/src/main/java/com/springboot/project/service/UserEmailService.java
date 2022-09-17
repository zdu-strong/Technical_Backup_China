package com.springboot.project.service;

import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.fasterxml.uuid.Generators;
import com.springboot.project.entity.UserEmailEntity;

@Service
public class UserEmailService extends BaseService {

    public void createUserEmailWithVerificationCode(String email, String userId, String verificationCode) {
        var userEntity = this.UserEntity().where(s -> s.getDeleteKey().equals("")).where(s -> s.getId().equals(userId))
                .getOnlyValue();
        UserEmailEntity userEmailEntity = new UserEmailEntity();
        userEmailEntity.setId(Generators.timeBasedGenerator().generate().toString());
        userEmailEntity.setEmail(email);
        userEmailEntity.setUser(userEntity);
        userEmailEntity.setCreateDate(new Date());
        userEmailEntity.setUpdateDate(new Date());
        userEmailEntity.setDeleteKey(Generators.timeBasedGenerator().generate().toString());
        userEmailEntity.setVerificationCode(verificationCode);

        this.entityManager.persist(userEmailEntity);
    }

    public void updateUserEmailWithVerificationCodePassed(String email, String userId, String verificationCode) {
        var userEmailEntity = this.UserEmailEntity().where(s -> s.getUser().getId().equals(userId))
                .where(s -> s.getUser().getDeleteKey().equals("")).where(s -> s.getEmail().equals(email))
                .where(s -> !s.getDeleteKey().equals("")).where(s -> s.getVerificationCode().equals(verificationCode))
                .sortedDescendingBy(s -> s.getId())
                .sortedDescendingBy(s -> s.getCreateDate())
                .findFirst().get();
        userEmailEntity.setCreateDate(new Date());
        userEmailEntity.setUpdateDate(new Date());
        userEmailEntity.setDeleteKey("");
        userEmailEntity.setVerificationCode(null);

        this.entityManager.merge(userEmailEntity);
    }

    public void checkEmailIsNotUsed(String email) {
        var isPresent = this.UserEmailEntity().where(s -> s.getDeleteKey().equals(""))
                .where(s -> s.getEmail().equals(email)).exists();
        if (isPresent) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "E-mail " + email + " has bound account");
        }
    }

    public void checkEmailVerificationCodeIsPassed(String email, String userId, String verificationCode) {
        if (StringUtils.isBlank(verificationCode)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The verification code of email " + email + " cannot be empty");
        }

        var stream = this.UserEmailEntity().where(s -> s.getUser().getId().equals(userId))
                .where(s -> s.getUser().getDeleteKey().equals("")).where(s -> s.getEmail().equals(email))
                .where(s -> !s.getDeleteKey().equals("")).where(s -> s.getVerificationCode().equals(verificationCode));
        if (!stream.exists()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The verification code of email " + email + " is wrong");
        }
    }
}
