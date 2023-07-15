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
public class OrganizeClosureEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private Date createDate;

    @Column(nullable = false)
    private Date updateDate;

    @Column(nullable = false)
    private Long gap;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY, optional = false)
    private OrganizeEntity ancestor;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY, optional = false)
    private OrganizeEntity descendant;

    public OrganizeClosureEntity setAncestor(OrganizeEntity ancestor) {
        if (this.ancestor != null) {
            this.ancestor.getDescendantList().remove(this);
        }
        this.ancestor = ancestor;
        if (this.ancestor != null) {
            this.ancestor.getDescendantList().add(this);
        }
        return this;
    }

    public OrganizeClosureEntity setDescendant(OrganizeEntity descendant) {
        if (this.descendant != null) {
            this.descendant.getAncestorList().remove(this);
        }
        this.descendant = descendant;
        if (this.descendant != null) {
            this.descendant.getAncestorList().add(this);
        }
        return this;
    }

}
