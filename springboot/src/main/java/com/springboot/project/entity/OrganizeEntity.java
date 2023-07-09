package com.springboot.project.entity;

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
public class OrganizeEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String deleteKey;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY, optional = false)
    private OrganizeShadowEntity organizeShadow;

    /**
     * split with ;
     */
    @Column(nullable = false, length = 1024 * 1024)
    private String path;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY, optional = true)
    private OrganizeEntity parentOrganize;

    @OneToMany(mappedBy = "parentOrganize", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrganizeEntity> childOrganizeList;

    public OrganizeEntity setParentOrganize(OrganizeEntity parentOrganize) {
        if (this.parentOrganize != null) {
            this.parentOrganize.getChildOrganizeList().remove(this);
        }
        this.parentOrganize = parentOrganize;
        if (this.parentOrganize != null) {
            this.parentOrganize.getChildOrganizeList().add(this);
        }
        return this;
    }

    public OrganizeEntity setOrganizeShadow(OrganizeShadowEntity organizeShadow) {
        if (this.organizeShadow != null) {
            this.organizeShadow.getOrganizeList().remove(this);
        }
        this.organizeShadow = organizeShadow;
        if (this.organizeShadow != null) {
            this.organizeShadow.getOrganizeList().add(this);
        }
        return this;
    }

}
