package com.springboot.project.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UserEmailModel {
    private String id;

    private String email;

    private UserModel user;

    private String verificationCode;

}
