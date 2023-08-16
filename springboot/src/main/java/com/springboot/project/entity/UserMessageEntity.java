package com.springboot.project.entity;

import java.util.Date;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(indexes = @Index(columnList = "folderName"))
@Getter
@Setter
@Accessors(chain = true)
public class UserMessageEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private Date createDate;

    @Column(nullable = false)
    private Date updateDate;

    @Column(nullable = false, length = 1024 * 1024 * 1024)
    @Lob
    @Basic(fetch = FetchType.LAZY)
    private String content;

    @Column(nullable = false)
    private Boolean isRecall;

    @Column(nullable = true)
    private String folderName;

    @Column(nullable = true)
    private Long folderSize;

    @Column(nullable = true, length = 1024 * 1024 * 1024)
    @Lob
    @Basic(fetch = FetchType.LAZY)
    private String fileName;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY, optional = false)
    private UserEntity user;

}
