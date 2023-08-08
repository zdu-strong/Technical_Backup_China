package com.springboot.project.entity;

import java.util.Date;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(indexes = @Index(columnList = "level"))
@Getter
@Setter
@Accessors(chain = true)
public class OrganizeEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private Date createDate;

    @Column(nullable = false)
    private Date updateDate;

    /**
     * init level is 0
     */
    @Column(nullable = false)
    private Long level;

    @Column(nullable = false)
    private Boolean isDeleted;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY, optional = false)
    private OrganizeShadowEntity organizeShadow;

    @OneToMany(mappedBy = "descendant", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<OrganizeClosureEntity> ancestorList;

    @OneToMany(mappedBy = "ancestor", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<OrganizeClosureEntity> descendantList;

}
