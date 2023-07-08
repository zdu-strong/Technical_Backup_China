package com.springboot.project.service;

import java.util.Date;
import org.springframework.stereotype.Service;
import com.fasterxml.uuid.Generators;
import com.springboot.project.entity.OrganizeRelationshipEntity;

@Service
public class OrganizeRelationshipService extends BaseService {

    public void createOrganizeRelationship(String organizeId) {
        this.createOrganizeRelationship(organizeId, organizeId, 0, 0);
    }

    public void createOrganizeRelationship(String parentOrganizeId, String childOrganizeId) {
        var levelOfAncestor = this.getLevelOfOrganize(parentOrganizeId);
        var levelOfChildOrganize = levelOfAncestor + 1;

        var ancestorOrganizeIdList = this.OrganizeRelationshipEntity()
                .where(s -> s.getDescendant().getId().equals(parentOrganizeId))
                .sortedDescendingBy(s -> s.getLevelOfAncestor()).select(s -> s.getAncestor().getId()).toList();

        this.createOrganizeRelationship(childOrganizeId, childOrganizeId, levelOfChildOrganize, levelOfChildOrganize);
        for (var ancestorOrganizeId : ancestorOrganizeIdList) {
            this.createOrganizeRelationship(ancestorOrganizeId, childOrganizeId, levelOfAncestor, levelOfChildOrganize);
            levelOfAncestor--;
        }
    }

    private int getLevelOfOrganize(String organizeId) {
        var level = this.OrganizeRelationshipEntity().where(s -> s.getDescendant().getId().equals(organizeId))
                .select(s -> s.getLevelOfDescendant()).findFirst().get();
        return level;
    }

    private void createOrganizeRelationship(String ancestorOrganizeId, String descendantOrganizeId,
            int levelOfAncestor, int levelOfDescendant) {
        var ancestorOrganize = this.OrganizeEntity().where(s -> s.getId().equals(ancestorOrganizeId)).getOnlyValue();
        var descendantOrganize = this.OrganizeEntity().where(s -> s.getId().equals(descendantOrganizeId))
                .getOnlyValue();
        var organizeRelationshipEntity = new OrganizeRelationshipEntity();
        organizeRelationshipEntity.setId(Generators.timeBasedGenerator().generate().toString());
        organizeRelationshipEntity.setCreateDate(new Date());
        organizeRelationshipEntity.setUpdateDate(new Date());
        organizeRelationshipEntity.setAncestor(ancestorOrganize);
        organizeRelationshipEntity.setDescendant(descendantOrganize);
        organizeRelationshipEntity.setLevelOfAncestor(levelOfAncestor);
        organizeRelationshipEntity.setLevelOfDescendant(levelOfDescendant);
        organizeRelationshipEntity.setGap(levelOfDescendant - levelOfAncestor);
        this.entityManager.persist(organizeRelationshipEntity);
    }

}
