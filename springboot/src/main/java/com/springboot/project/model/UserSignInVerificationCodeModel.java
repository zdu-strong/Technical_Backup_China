package com.springboot.project.model;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UserSignInVerificationCodeModel {
    private String id;
    private String verificationCode;
    private Date createDate;
    private Date updateDate;
    private UserModel user;
}
