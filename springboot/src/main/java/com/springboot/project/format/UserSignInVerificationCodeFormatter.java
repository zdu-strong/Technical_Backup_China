package com.springboot.project.format;

import org.springframework.stereotype.Service;
import com.springboot.project.entity.UserSignInVerificationCodeEntity;
import com.springboot.project.model.UserModel;
import com.springboot.project.model.UserSignInVerificationCodeModel;
import com.springboot.project.service.BaseService;

@Service
public class UserSignInVerificationCodeFormatter extends BaseService {

    public UserSignInVerificationCodeModel format(UserSignInVerificationCodeEntity userSignInVerificationCodeEntity) {
        var userSignInVerificationCodeModel = new UserSignInVerificationCodeModel()
                .setId(userSignInVerificationCodeEntity.getId())
                .setVerificationCode(userSignInVerificationCodeEntity.getVerificationCode())
                .setCreateDate(userSignInVerificationCodeEntity.getCreateDate())
                .setUpdateDate(userSignInVerificationCodeEntity.getUpdateDate())
                .setUser(new UserModel().setId(userSignInVerificationCodeEntity.getUser().getId()));
        return userSignInVerificationCodeModel;
    }

}
