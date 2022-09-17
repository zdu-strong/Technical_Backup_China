package com.springboot.project.service;

import java.time.Duration;
import java.util.Calendar;
import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.uuid.Generators;
import com.springboot.project.model.UserSignInVerificationCodeModel;
import com.springboot.project.entity.UserSignInVerificationCodeEntity;

@Service
public class UserSignInVerificationCodeService extends BaseService {

    public UserSignInVerificationCodeModel createVerificationCodeForSignIn(String userId) {
        var userEntity = this.UserEntity().where(s -> s.getId().equals(userId)).where(s -> s.getHasRegistered())
                .where(s -> s.getDeleteKey().equals("")).getOnlyValue();
        var userSignInVerificationCodeEntity = new UserSignInVerificationCodeEntity();
        userSignInVerificationCodeEntity.setId(Generators.timeBasedGenerator().generate().toString());

        {
            var verificationCode = "";
            while (true) {
                if (verificationCode.length() >= 5) {
                    break;
                }
                Integer number = Double.valueOf(Math.floor(Math.random() * 10)).intValue();
                if (verificationCode.length() == 0 && number <= 0) {
                    continue;
                }
                verificationCode = verificationCode + number.toString();
            }
            userSignInVerificationCodeEntity.setVerificationCode(verificationCode);
        }

        userSignInVerificationCodeEntity.setCreateDate(new Date());
        userSignInVerificationCodeEntity.setUpdateDate(new Date());
        userSignInVerificationCodeEntity.setUser(userEntity);

        this.entityManager.persist(userSignInVerificationCodeEntity);

        return this.userSignInVerificationCodeFormatter.format(userSignInVerificationCodeEntity);
    }

    public void checkUserSignInVerificationCode(String userId,
            UserSignInVerificationCodeModel userSignInVerificationCodeModel) {
        var id = userSignInVerificationCodeModel.getId();
        var userSignInVerificationCodeEntity = this.UserSignInVerificationCodeEntity()
                .where(s -> s.getId().equals(id)).getOnlyValue();
        if (!userSignInVerificationCodeEntity.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect password");
        }

        if (!userSignInVerificationCodeEntity.getVerificationCode()
                .equals(userSignInVerificationCodeModel.getVerificationCode())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect password");
        }

        Duration duration = Duration.ofHours(1);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MILLISECOND, Long.valueOf(0 - duration.toMillis()).intValue());
        Date expireDate = calendar.getTime();
        if (userSignInVerificationCodeEntity.getCreateDate().before(expireDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect password");
        }

        userSignInVerificationCodeEntity.setUser(null);
        this.entityManager.merge(userSignInVerificationCodeEntity);
        this.entityManager.remove(userSignInVerificationCodeEntity);
    }

}
