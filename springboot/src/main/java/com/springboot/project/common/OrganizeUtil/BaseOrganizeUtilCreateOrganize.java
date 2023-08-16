package com.springboot.project.common.OrganizeUtil;

import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.jinq.orm.stream.JinqStream;
import org.springframework.stereotype.Service;
import com.fasterxml.uuid.Generators;
import com.springboot.project.entity.OrganizeEntity;
import com.springboot.project.entity.OrganizeShadowEntity;
import com.springboot.project.model.OrganizeModel;

@Service
public class BaseOrganizeUtilCreateOrganize extends BaseOrganizeUtil {

    public OrganizeModel createOrganizeToStart(OrganizeModel organizeModel) {
        var organizeShadowId = this.organizeShadowService.createOrganizeShadow(organizeModel);
        var organizeShadow = this.OrganizeShadowEntity().where(s -> s.getId().equals(organizeShadowId)).getOnlyValue();

        var parentOrganizeId = organizeModel.getParentOrganize() != null
                ? organizeModel.getParentOrganize().getId()
                : null;
        var parentOrganize = StringUtils.isNotBlank(parentOrganizeId)
                ? this.OrganizeEntity().where(s -> s.getId().equals(parentOrganizeId))
                        .where(s -> !JinqStream.from(s.getAncestorList())
                                .where(m -> m.getAncestor().getIsDeleted()).exists())
                        .getOnlyValue()
                : null;

        var organizeEntity = this.createOrganizeEntity(organizeShadow,
                parentOrganize == null ? 0 : parentOrganize.getLevel() + 1);
        this.organizeClosureService.createOrganizeClosure(organizeEntity.getId(), organizeEntity.getId());

        return this.organizeFormatter.format(organizeEntity);
    }

    public OrganizeModel createOrganizeToEnd(String organizeId) {
        var organizeEntity = this.OrganizeEntity()
                .where(s -> s.getId().equals(organizeId))
                .getOnlyValue();

        organizeEntity.setIsDeleted(false);
        organizeEntity.setUpdateDate(new Date());
        organizeEntity.getOrganizeShadow().setIsDeleted(false);
        organizeEntity.getOrganizeShadow().setUpdateDate(new Date());
        this.merge(organizeEntity);

        return this.organizeFormatter.format(organizeEntity);
    }

    protected OrganizeEntity createOrganizeEntity(OrganizeShadowEntity organizeShadow, Long level) {
        var organizeEntity = new OrganizeEntity();
        organizeEntity.setId(Generators.timeBasedGenerator().generate().toString());
        organizeEntity.setCreateDate(organizeShadow.getCreateDate());
        organizeEntity.setUpdateDate(new Date());
        organizeEntity.setLevel(level);
        organizeEntity.setIsDeleted(true);
        organizeEntity.setOrganizeShadow(organizeShadow);
        this.persist(organizeEntity);

        return organizeEntity;
    }
}
