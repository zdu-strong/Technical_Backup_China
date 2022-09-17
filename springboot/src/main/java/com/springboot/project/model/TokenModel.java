package com.springboot.project.model;

import cn.hutool.crypto.asymmetric.RSA;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class TokenModel {
    private String access_token;
    private UserModel userModel;
    private RSA RSA;
}
