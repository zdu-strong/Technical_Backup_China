package com.springboot.project.model;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class FriendshipModel {
    private String id;

    private Boolean isFriend;

    private Boolean isBlacklist;

    private Boolean isFriendOfFriend;

    private Boolean isBlacklistOfFriend;

    private Date createDate;

    private Date updateDate;

    private UserModel user;

    private UserModel friend;

    private String aesOfUser;

    private String aesOfFriend;

}
