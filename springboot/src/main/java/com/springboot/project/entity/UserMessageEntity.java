package com.springboot.project.entity;

import java.util.Date;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
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

    @Column(nullable = false, length = 1024 * 4)
    private String content;

    @Column(nullable = false)
    private Boolean isRecall;

    @Column(nullable = true)
    private String folderName;

    @Column(nullable = true)
    private Long folderSize;

    @Column(nullable = true, length = 1024 * 4)
    private String fileName;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY, optional = false)
    private UserEntity user;

}
