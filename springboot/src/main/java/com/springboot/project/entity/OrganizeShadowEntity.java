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
public class OrganizeShadowEntity {

    @Id
    private String id;

    @Column(nullable = true, length = 1024 * 1024)
    private String name;

    @Column(nullable = false)
    private Date createDate;

    @Column(nullable = false)
    private Date updateDate;

    @OneToMany(mappedBy = "organizeShadow", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrganizeEntity> organizeList;

}
