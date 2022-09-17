package com.springboot.project.entity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Getter
@Setter
@Accessors(chain = true)
public class EncryptDecryptEntity {
    @Id
    private String id;

    @Column(nullable = false, length = 1024 * 1024)
    private String publicKeyOfRSA;

    @Column(nullable = false, length = 1024 * 1024)
    private String privateKeyOfRSA;

    @Column(nullable = false, length = 1024 * 1024)
    private String secretKeyOfAES;

    @Column(nullable = false)
    private Date createDate;

    @Column(nullable = false)
    private Date updateDate;
}
