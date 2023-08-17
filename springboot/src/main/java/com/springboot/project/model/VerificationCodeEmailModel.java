package com.springboot.project.model;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class VerificationCodeEmailModel {

    private String id;
    private String email;
    private String verificationCode;
    private Integer verificationCodeLength;
    private Boolean isDeleted;
    private Date createDate;
    private Date updateDate;

}
