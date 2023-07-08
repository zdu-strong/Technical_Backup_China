package com.springboot.project.entity;

import java.util.Date;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Getter
@Setter
@Accessors(chain = true)
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "ancestor_id", "descendant_id" }) })
public class OrganizeRelationshipEntity {

    @Id
    private String id;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY, optional = false)
    private OrganizeEntity ancestor;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY, optional = false)
    private OrganizeEntity descendant;

    @Column(nullable = false)
    private Integer gap;

    /**
     * init level is 0
     */
    @Column(nullable = false)
    private Integer levelOfAncestor;

    /**
     * init level is 0
     */
    @Column(nullable = false)
    private Integer levelOfDescendant;

    @Column(nullable = false)
    private Date createDate;

    @Column(nullable = false)
    private Date updateDate;

    public OrganizeRelationshipEntity setAncestor(OrganizeEntity ancestor) {
        if (this.ancestor != null) {
            this.ancestor.getDescendantList().remove(this);
        }
        this.ancestor = ancestor;
        if (this.ancestor != null) {
            this.ancestor.getDescendantList().add(this);
        }
        return this;
    }

    public OrganizeRelationshipEntity setDescendant(OrganizeEntity descendant) {
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
