package com.springboot.project.entity;

import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
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
    private Boolean isBlacklist;

    @Column(nullable = false)
    private Date createDate;

    @Column(nullable = false)
    private Date updateDate;

    @Column(nullable = false, length = 1024 * 1024 * 1024)
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
