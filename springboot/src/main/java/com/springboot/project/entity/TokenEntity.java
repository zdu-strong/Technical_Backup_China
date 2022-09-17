package com.springboot.project.entity;

import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
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

    @Column(unique = true)
    private String jwtId;

    @Column(nullable = false, length = 1024 * 1024 * 1024)
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
