package com.springboot.project.entity;

import java.util.Date;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
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

    @Column(nullable = false, length = 1024 * 4)
    private String name;

    @Column(nullable = false)
    private Boolean isDeleted;

    @Column(nullable = false)
    private Date createDate;

    @Column(nullable = false)
    private Date updateDate;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY, optional = true)
    private OrganizeShadowEntity parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrganizeShadowEntity> childList;

    @OneToMany(mappedBy = "organizeShadow", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrganizeEntity> organizeList;

    public OrganizeShadowEntity setParent(OrganizeShadowEntity parent) {
        if (this.parent != null) {
            this.parent.getChildList().remove(this);
        }
        this.parent = parent;
        if (this.parent != null) {
            this.parent.getChildList().add(this);
        }
        return this;
    }

}
