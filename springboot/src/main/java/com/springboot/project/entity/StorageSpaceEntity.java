package com.springboot.project.entity;

import java.util.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(indexes = @Index(columnList = "folderName"))
@Getter
@Setter
@Accessors(chain = true)
public class StorageSpaceEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String folderName;

    @Column(nullable = false)
    private Date createDate;

    @Column(nullable = false)
    private Date updateDate;

}
