package com.springboot.project.entity;

import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Getter
@Setter
@Accessors(chain = true)
public class UserEntity {
    @Id
    private String id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private Date createDate;

    @Column(nullable = false)
    private Date updateDate;

    @Column(nullable = false)
    private String deleteKey;

    @Column(nullable = true, length = 1024 * 1024 * 1024)
    private String publicKeyOfRSA;

    @Column(nullable = true, length = 1024 * 1024 * 1024)
    private String privateKeyOfRSA;

    @Column(nullable = true, length = 1024 * 1024 * 1024)
    private String password;

    @Column(nullable = false)
    private Boolean hasRegistered;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserEmailEntity> userEmailList;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserMessageEntity> userMessageList;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserSignInVerificationCodeEntity> userSignInVerificationCodeList;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TokenEntity> tokenList;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FriendshipEntity> fridendList;

    @OneToMany(mappedBy = "friend", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FriendshipEntity> reverseFridendList;

}
