package com.springboot.project.service;

import com.fasterxml.uuid.Generators;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import com.springboot.project.model.OrganizeModel;
import com.springboot.project.entity.*;

@Service
public class OrganizeShadowService extends BaseService {

    public String createOrganizeShadow(OrganizeModel organizeModel) {
        var parentOrganizeId = organizeModel.getParentOrganize() != null ? organizeModel.getParentOrganize().getId()
                : null;
        var parentOrganizeShadow = StringUtils.isNotBlank(parentOrganizeId)
                ? this.OrganizeEntity().where(s -> s.getId().equals(parentOrganizeId))
                        .select(s -> s.getOrganizeShadow())
                        .getOnlyValue()
                : null;
        var organizeShadowEntity = new OrganizeShadowEntity();
        organizeShadowEntity.setId(Generators.timeBasedGenerator().generate().toString());
        organizeShadowEntity.setName(organizeModel.getName());
        organizeShadowEntity.setIsDeleted(true);
        organizeShadowEntity.setCreateDate(new Date());
        organizeShadowEntity.setUpdateDate(new Date());
        organizeShadowEntity.setParent(parentOrganizeShadow);
        this.persist(organizeShadowEntity);

        return organizeShadowEntity.getId();
    }

}
