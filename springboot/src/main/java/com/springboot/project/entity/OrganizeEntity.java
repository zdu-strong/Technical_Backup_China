package com.springboot.project.entity;

import java.util.Date;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Getter
@Setter
@Accessors(chain = true)
public class OrganizeEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Date createDate;

    @Column(nullable = false)
    private Date updateDate;

    @Column(nullable = false)
    private String deleteKey;

    @OneToMany(mappedBy = "descendant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrganizeRelationshipEntity> ancestorList;

    @OneToMany(mappedBy = "ancestor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrganizeRelationshipEntity> descendantList;

}
