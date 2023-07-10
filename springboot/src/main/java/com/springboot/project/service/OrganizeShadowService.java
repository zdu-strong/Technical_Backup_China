package com.springboot.project.service;

import com.fasterxml.uuid.Generators;
import com.google.common.collect.Lists;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import com.springboot.project.model.OrganizeModel;
import com.springboot.project.entity.*;

@Service
public class OrganizeShadowService extends BaseService {

    protected String createOrganizeShadow(OrganizeModel organizeModel) {
        var parentOrganizeId = organizeModel.getParentOrganize() != null ? organizeModel.getParentOrganize().getId()
                : null;
        var parentOrganize = StringUtils.isNotBlank(parentOrganizeId)
                ? this.OrganizeEntity().where(s -> s.getId().equals(parentOrganizeId)).getOnlyValue()
                : null;
        var organizeShadowEntity = new OrganizeShadowEntity();
        organizeShadowEntity.setId(Generators.timeBasedGenerator().generate().toString());
        organizeShadowEntity.setName(organizeModel.getName());
        organizeShadowEntity.setDeleteKey("");
        organizeShadowEntity.setCreateDate(new Date());
        organizeShadowEntity.setUpdateDate(new Date());
        organizeShadowEntity.setParent(parentOrganize != null ? parentOrganize.getOrganizeShadow() : null);
        organizeShadowEntity.setChildList(Lists.newArrayList());
        organizeShadowEntity.setOrganizeList(Lists.newArrayList());
        this.entityManager.persist(organizeShadowEntity);

        return organizeShadowEntity.getId();
    }
}
