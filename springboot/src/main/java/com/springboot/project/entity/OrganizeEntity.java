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

    /**
     * init level is 0
     */
    @Column(nullable = false)
    private Long level;

    @Column(nullable = false)
    private String deleteKey;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY, optional = false)
    private OrganizeShadowEntity organizeShadow;

    @OneToMany(mappedBy = "descendant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrganizeClosureEntity> ancestorList;

    @OneToMany(mappedBy = "ancestor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrganizeClosureEntity> descendantList;

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
