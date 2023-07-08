package com.springboot.project.service;

import com.fasterxml.uuid.Generators;
import com.google.common.collect.Lists;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.jinq.orm.stream.JinqStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.springboot.project.model.OrganizeModel;
import com.springboot.project.entity.*;

@Service
public class OrganizeService extends BaseService {

    @Autowired
    private OrganizeRelationshipService organizeRelationshipService;

    public Boolean isChildOrganize(String childOrganizeId, String parentOrganizeId) {
        return this.OrganizeEntity().where(s -> s.getId().equals(childOrganizeId))
                .where(s -> !JinqStream.from(s.getAncestorList()).where(m -> !m.getAncestor().getDeleteKey().equals(""))
                        .exists())
                .where(s -> JinqStream.from(s.getAncestorList())
                        .where(m -> m.getAncestor().getId().equals(parentOrganizeId))
                        .exists())
                .findOne().isPresent();
    }

    public OrganizeModel createOrganize(OrganizeModel organizeModel) {
        var parentOrganizeId = organizeModel.getParentOrganize() != null
                ? organizeModel.getParentOrganize().getId()
                : null;
        var parentOrganize = StringUtils.isNotBlank(parentOrganizeId)
                ? this.OrganizeEntity().where(s -> s.getId().equals(parentOrganizeId))
                        .where(s -> !JinqStream.from(s.getAncestorList())
                                .where(m -> !m.getAncestor().getDeleteKey().equals(""))
                                .exists())
                        .getOnlyValue()
                : null;
        var organize = new OrganizeEntity();
        organize.setId(Generators.timeBasedGenerator().generate().toString());
        organize.setName(organizeModel.getName());
        organize.setCreateDate(new Date());
        organize.setUpdateDate(new Date());
        organize.setDeleteKey("");
        organize.setAncestorList(Lists.newArrayList());
        organize.setDescendantList(Lists.newArrayList());
        this.entityManager.persist(organize);

        if (parentOrganize != null) {
            this.organizeRelationshipService.createOrganizeRelationship(parentOrganize.getId(), organize.getId());
        } else {
            this.organizeRelationshipService.createOrganizeRelationship(organize.getId());
        }

        return this.organizeFormatter.format(organize);
    }

    public void deleteOrganize(String id) {
        var organize = this.OrganizeEntity().where(s -> s.getId().equals(id))
                .where(s -> !JinqStream.from(s.getAncestorList()).where(m -> !m.getAncestor().getDeleteKey().equals(""))
                        .exists())
                .getOnlyValue();
        organize.setUpdateDate(new Date());
        organize.setDeleteKey(Generators.timeBasedGenerator().generate().toString());
        this.entityManager.merge(organize);
    }

    public OrganizeModel getOrganize(String id) {
        var organize = this.OrganizeEntity().where(s -> s.getId().equals(id))
                .where(s -> !JinqStream.from(s.getAncestorList()).where(m -> !m.getAncestor().getDeleteKey().equals(""))
                        .exists())
                .getOnlyValue();
        return this.organizeFormatter.format(organize);
    }
}
