package com.springboot.project.entity;

import java.util.Date;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
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

    @Column(nullable = false, length = 1024 * 1024 * 1024)
    @Lob
    @Basic(fetch = FetchType.LAZY)
    private String publicKeyOfRSA;

    @Column(nullable = false, length = 1024 * 1024 * 1024)
    @Lob
    @Basic(fetch = FetchType.LAZY)
    private String privateKeyOfRSA;

    @Column(nullable = false, length = 1024 * 1024 * 1024)
    @Lob
    @Basic(fetch = FetchType.LAZY)
    private String secretKeyOfAES;

    @Column(nullable = false)
    private Date createDate;

    @Column(nullable = false)
    private Date updateDate;

}
