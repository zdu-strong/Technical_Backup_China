package com.springboot.project.service;

import java.util.Date;
import org.springframework.stereotype.Service;
import com.fasterxml.uuid.Generators;
import com.springboot.project.entity.*;

@Service
public class OrganizeClosureService extends BaseService {

    protected void createOrganizeClosure(String ancestorId, String descendantId) {
        var ancestor = this.OrganizeEntity().where(s -> s.getId().equals(ancestorId)).getOnlyValue();
        var descendant = this.OrganizeEntity().where(s -> s.getId().equals(descendantId)).getOnlyValue();

        var organizeClosureEntity = new OrganizeClosureEntity();
        organizeClosureEntity.setId(Generators.timeBasedGenerator().generate().toString());
        organizeClosureEntity.setCreateDate(new Date());
        organizeClosureEntity.setUpdateDate(new Date());
        organizeClosureEntity.setAncestor(ancestor);
        organizeClosureEntity.setDescendant(descendant);
        organizeClosureEntity.setGap(descendant.getLevel() - ancestor.getLevel());
        this.entityManager.persist(organizeClosureEntity);
    }

}