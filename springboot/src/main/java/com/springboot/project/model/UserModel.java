package com.springboot.project.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UserModel {
	private String id;
	private String username;
	private String email;
    private String password;
    private String publicKeyOfRSA;
    private String privateKeyOfRSA;
    private Boolean hasRegistered;
    private UserSignInVerificationCodeModel userSignInVerificationCode;
    private List<UserEmailModel> userEmailList;
}
