package com.springboot.project.service;

import com.fasterxml.uuid.Generators;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import com.springboot.project.model.OrganizeModel;
import com.beust.jcommander.internal.Lists;
import com.springboot.project.common.database.JPQLFunction;
import com.springboot.project.entity.*;

@Service
public class OrganizeService extends BaseService {
    public Boolean isChildOrganize(String childOrganizeId, String parentOrganizeId) {
        return this.OrganizeEntity().where(organize -> organize.getId().equals(childOrganizeId))
                .where(organize -> JPQLFunction.isChildOrganize(organize.getId(), parentOrganizeId))
                .findOne().isPresent();
    }

    public OrganizeModel createOrganize(OrganizeModel organizeModel) {
        var parentOrganizeId = organizeModel.getParentOrganize() != null
                ? organizeModel.getParentOrganize().getId()
                : null;
        var parentOrganize = StringUtils.isNotBlank(parentOrganizeId) ? this
                .OrganizeEntity()
                .where(s -> JPQLFunction.isNotDeleteOfOrganizeAndAncestors(parentOrganizeId))
                .where(organize -> organize.getId().equals(parentOrganizeId))
                .getOnlyValue() : null;
        var organize = new OrganizeEntity();
        organize.setId(Generators.timeBasedGenerator().generate().toString());
        organize.setName(organizeModel.getName());
        organize.setCreateDate(new Date());
        organize.setUpdateDate(new Date());
        organize.setDeleteKey("");
        organize.setParentOrganize(parentOrganize);
        organize.setChildOrganizeList(Lists.newArrayList());
        this.entityManager.persist(organize);
        return this.organizeFormatter.format(organize);
    }

    public void deleteOrganize(String id) {
        var organize = this.OrganizeEntity()
                .where(s -> JPQLFunction.isNotDeleteOfOrganizeAndAncestors(s.getId()))
                .where(s -> s.getId().equals(id))
                .getOnlyValue();
        organize.setUpdateDate(new Date());
        organize.setDeleteKey(Generators.timeBasedGenerator().generate().toString());
        this.entityManager.merge(organize);
    }

    public OrganizeModel getOrganize(String id) {
        var organize = this.OrganizeEntity()
                .where(s -> JPQLFunction.isNotDeleteOfOrganizeAndAncestors(s.getId()))
                .where(s -> s.getId().equals(id))
                .getOnlyValue();
        return this.organizeFormatter.format(organize);
    }
}
