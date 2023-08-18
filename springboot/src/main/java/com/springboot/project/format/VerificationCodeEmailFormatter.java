package com.springboot.project.format;

import org.springframework.stereotype.Service;
import com.springboot.project.entity.*;
import com.springboot.project.model.VerificationCodeEmailModel;
import com.springboot.project.service.BaseService;

@Service
public class VerificationCodeEmailFormatter extends BaseService {

    public VerificationCodeEmailModel format(VerificationCodeEmailEntity verificationCodeEmailEntity) {
        var verificationCodeEmailModel = new VerificationCodeEmailModel()
                .setId(verificationCodeEmailEntity.getId())
                .setEmail(verificationCodeEmailEntity.getEmail())
                .setVerificationCode(verificationCodeEmailEntity.getVerificationCode())
                .setVerificationCodeLength(verificationCodeEmailEntity.getVerificationCode().length())
                .setHasUsed(verificationCodeEmailEntity.getHasUsed())
                .setIsPassed(verificationCodeEmailEntity.getIsPassed())
                .setCreateDate(verificationCodeEmailEntity.getCreateDate())
                .setUpdateDate(verificationCodeEmailEntity.getUpdateDate());
        return verificationCodeEmailModel;
    }
}
