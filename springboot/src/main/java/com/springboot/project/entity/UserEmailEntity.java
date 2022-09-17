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
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "deleteKey", "email" }) })
@Getter
@Setter
@Accessors(chain = true)
public class UserEmailEntity {
    @Id
    private String id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private Date createDate;

    @Column(nullable = false)
    private Date updateDate;

    @Column(nullable = false)
    private String deleteKey;

    @Column(nullable = true)
    private String verificationCode;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY, optional = false)
    private UserEntity user;

    public UserEmailEntity setUser(UserEntity user) {
        if (this.user != null) {
            this.user.getUserEmailList().remove(this);
        }
        this.user = user;
        if (this.user != null) {
            this.user.getUserEmailList().add(this);
        }
        return this;
    }

}
