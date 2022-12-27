package com.springboot.project.model;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UserMessageModel {
    private String id;

    private Boolean isDelete;

    private Boolean isRecall;

    private Date createDate;

    private Date updateDate;

    private String content;

    private UserModel user;

    private Long totalPage;

    private Integer pageNum;

    private String url;
}
