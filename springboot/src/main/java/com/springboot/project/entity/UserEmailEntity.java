package com.springboot.project.entity;

import java.util.Date;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
