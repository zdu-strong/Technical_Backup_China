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
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Getter
@Setter
@Accessors(chain = true)
public class TokenEntity {

    @Id
    private String id;

    @Column(unique = false)
    private String jwtId;

    @Column(nullable = false, length = 1024 * 1024 * 1024)
    @Lob
    @Basic(fetch = FetchType.LAZY)
    private String privateKeyOfRSA;

    @Column(nullable = false)
    private Date createDate;

    @Column(nullable = false)
    private Date updateDate;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY, optional = false)
    private UserEntity user;

    public TokenEntity setUser(UserEntity user) {
        if (this.user != null) {
            this.user.getTokenList().remove(this);
        }
        this.user = user;
        if (this.user != null) {
            this.user.getTokenList().add(this);
        }
        return this;
    }

}
