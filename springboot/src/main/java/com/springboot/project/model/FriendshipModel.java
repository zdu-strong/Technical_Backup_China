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

    private Boolean isInBlacklist;

    private Boolean isFriendOfFriend;

    private Boolean isInBlacklistOfFriend;

    private Date createDate;

    private Date updateDate;

    private UserModel user;

    private UserModel friend;

    private Boolean hasInitiative;

    private String aesOfUser;

    private String aesOfFriend;

}
