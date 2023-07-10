package com.springboot.project.service;

import com.fasterxml.uuid.Generators;
import com.google.common.collect.Lists;
import java.util.Date;
import org.springframework.stereotype.Service;
import com.springboot.project.model.OrganizeModel;
import com.springboot.project.entity.*;

@Service
public class OrganizeShadowService extends BaseService {

    protected String createOrganizeShadow(OrganizeModel organizeModel) {
        var organizeShadowEntity = new OrganizeShadowEntity();
        organizeShadowEntity.setId(Generators.timeBasedGenerator().generate().toString());
        organizeShadowEntity.setName(organizeModel.getName());
        organizeShadowEntity.setCreateDate(new Date());
        organizeShadowEntity.setUpdateDate(new Date());
        organizeShadowEntity.setOrganizeList(Lists.newArrayList());
        this.entityManager.persist(organizeShadowEntity);

        return organizeShadowEntity.getId();
    }
}
