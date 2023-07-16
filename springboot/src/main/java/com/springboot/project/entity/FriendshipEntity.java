package com.springboot.project.entity;

import java.util.Date;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "user_id", "friend_id" }) })
@Getter
@Setter
@Accessors(chain = true)
public class FriendshipEntity {
    @Id
    private String id;

    @Column(nullable = false)
    private Boolean isFriend;

    @Column(nullable = false)
    private Boolean isInBlacklist;

    @Column(nullable = false)
    private Date createDate;

    @Column(nullable = false)
    private Date updateDate;

    @Column(nullable = false)
    private Boolean hasInitiative;

    @Column(nullable = false, length = 1024 * 1024 * 1024)
    @Lob
    @Basic(fetch = FetchType.LAZY)
    private String secretKeyOfAES;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY, optional = false)
    private UserEntity user;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY, optional = false)
    private UserEntity friend;

    public FriendshipEntity setUser(UserEntity user) {
        if (this.user != null) {
            this.user.getFridendList().remove(this);
        }
        this.user = user;
        if (this.user != null) {
            this.user.getFridendList().add(this);
        }
        return this;
    }

    public FriendshipEntity setFriend(UserEntity friend) {
        if (this.friend != null) {
            this.friend.getReverseFridendList().remove(this);
        }
        this.friend = friend;
        if (this.friend != null) {
            this.friend.getReverseFridendList().add(this);
        }
        return this;
    }

}
