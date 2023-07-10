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
    private OrganizeShadowService organizeShadowService;

    @Autowired
    private OrganizeClosureService organizeClosureService;

    public OrganizeModel createOrganize(OrganizeModel organizeModel) {
        var organizeShadowId = this.organizeShadowService.createOrganizeShadow(organizeModel);
        var organizeShadow = this.OrganizeShadowEntity().where(s -> s.getId().equals(organizeShadowId)).getOnlyValue();

        var parentOrganizeId = organizeModel.getParentOrganize() != null
                ? organizeModel.getParentOrganize().getId()
                : null;
        var parentOrganize = StringUtils.isNotBlank(parentOrganizeId)
                ? this.OrganizeEntity().where(s -> s.getId().equals(parentOrganizeId))
                        .where(s -> !JinqStream.from(s.getAncestorList())
                                .where(m -> !m.getAncestor().getDeleteKey().equals("")).exists())
                        .getOnlyValue()
                : null;

        var organizeEntity = new OrganizeEntity();
        organizeEntity.setId(Generators.timeBasedGenerator().generate().toString());
        organizeEntity.setLevel(parentOrganize == null ? 0 : parentOrganize.getLevel() + 1);
        organizeEntity.setDeleteKey("");
        organizeEntity.setOrganizeShadow(organizeShadow);
        organizeEntity.setAncestorList(Lists.newArrayList());
        organizeEntity.setDescendantList(Lists.newArrayList());
        this.entityManager.persist(organizeEntity);

        this.organizeClosureService.createOrganizeClosure(organizeEntity.getId(), organizeEntity.getId());

        if (parentOrganize != null) {
            var ancestorIdList = this.OrganizeClosureEntity()
                    .where(s -> s.getDescendant().getId().equals(parentOrganizeId))
                    .select(s -> s.getAncestor().getId())
                    .toList();
            for (var ancestorId : ancestorIdList) {
                this.organizeClosureService.createOrganizeClosure(ancestorId, organizeEntity.getId());
            }
        }

        return this.organizeFormatter.format(organizeEntity);
    }

    public OrganizeModel moveOrganize(String organizeId, String parentOrganizeId) {
        var organizeEntity = this.OrganizeEntity().where(s -> s.getId().equals(organizeId))
                .where(s -> !JinqStream.from(s.getAncestorList()).where(m -> !m.getAncestor().getDeleteKey().equals(""))
                        .exists())
                .getOnlyValue();
        var parentOrganize = StringUtils.isNotBlank(parentOrganizeId)
                ? this.OrganizeEntity().where(s -> s.getId().equals(parentOrganizeId))
                        .where(s -> !JinqStream.from(s.getAncestorList())
                                .where(m -> !m.getAncestor().getDeleteKey().equals(""))
                                .exists())
                        .getOnlyValue()
                : null;

        var targetOrganizeEntity = new OrganizeEntity();
        targetOrganizeEntity.setId(Generators.timeBasedGenerator().generate().toString());
        targetOrganizeEntity.setLevel(parentOrganize == null ? 0 : parentOrganize.getLevel() + 1);
        targetOrganizeEntity.setDeleteKey(Generators.timeBasedGenerator().generate().toString());
        targetOrganizeEntity.setOrganizeShadow(organizeEntity.getOrganizeShadow());
        targetOrganizeEntity.setAncestorList(Lists.newArrayList());
        targetOrganizeEntity.setDescendantList(Lists.newArrayList());
        this.entityManager.persist(organizeEntity);

        this.organizeClosureService.createOrganizeClosure(targetOrganizeEntity.getId(), targetOrganizeEntity.getId());

        if (parentOrganize != null) {
            var ancestorIdList = this.OrganizeClosureEntity()
                    .where(s -> s.getDescendant().getId().equals(parentOrganizeId))
                    .select(s -> s.getAncestor().getId())
                    .toList();
            for (var ancestorId : ancestorIdList) {
                this.organizeClosureService.createOrganizeClosure(ancestorId, targetOrganizeEntity.getId());
            }
        }

        this.moveChildOrganizeList(organizeId, targetOrganizeEntity.getId());

        organizeEntity.setDeleteKey(Generators.timeBasedGenerator().generate().toString());
        this.entityManager.merge(organizeEntity);
        targetOrganizeEntity.setDeleteKey("");
        this.entityManager.merge(targetOrganizeEntity);

        return this.organizeFormatter.format(targetOrganizeEntity);
    }

    private void moveChildOrganizeList(String sourceOrganizeId, String targetOrganizeId) {
        var sourceChildOrganizeIdList = this.OrganizeClosureEntity()
                .where(s -> s.getAncestor().getId().equals(sourceOrganizeId))
                .where(s -> s.getGap() == 1)
                .where(s -> s.getDescendant().getDeleteKey().equals(""))
                .map(s -> s.getDescendant().getId())
                .toList();

        for (var sourceChildOrganizeId : sourceChildOrganizeIdList) {
            var parentOrganize = this.OrganizeEntity().where(s -> s.getId().equals(targetOrganizeId))
                    .getOnlyValue();
            var sourceChildOrganizeEntity = this.OrganizeEntity().where(s -> s.getId().equals(sourceChildOrganizeId))
                    .getOnlyValue();

            var childTargetOrganizeEntity = new OrganizeEntity();
            childTargetOrganizeEntity.setId(Generators.timeBasedGenerator().generate().toString());
            childTargetOrganizeEntity.setLevel(parentOrganize.getLevel() + 1);
            childTargetOrganizeEntity.setDeleteKey("");
            childTargetOrganizeEntity.setOrganizeShadow(sourceChildOrganizeEntity.getOrganizeShadow());
            childTargetOrganizeEntity.setAncestorList(Lists.newArrayList());
            childTargetOrganizeEntity.setDescendantList(Lists.newArrayList());
            this.entityManager.persist(childTargetOrganizeEntity);

            this.organizeClosureService.createOrganizeClosure(childTargetOrganizeEntity.getId(),
                    childTargetOrganizeEntity.getId());

            var ancestorIdList = this.OrganizeClosureEntity()
                    .where(s -> s.getDescendant().getId().equals(targetOrganizeId))
                    .select(s -> s.getAncestor().getId())
                    .toList();
            for (var ancestorId : ancestorIdList) {
                this.organizeClosureService.createOrganizeClosure(ancestorId, childTargetOrganizeEntity.getId());
            }

            this.moveChildOrganizeList(sourceChildOrganizeId, childTargetOrganizeEntity.getId());
        }

    }

    public void deleteOrganize(String id) {
        var organizeEntity = this.OrganizeEntity().where(s -> s.getId().equals(id))
                .where(s -> !JinqStream.from(s.getAncestorList()).where(m -> !m.getAncestor().getDeleteKey().equals(""))
                        .exists())
                .getOnlyValue();
        organizeEntity.getOrganizeShadow().setUpdateDate(new Date());
        organizeEntity.setDeleteKey(Generators.timeBasedGenerator().generate().toString());
        this.entityManager.merge(organizeEntity);
    }

    public OrganizeModel getOrganize(String id) {
        var organizeEntity = this.OrganizeEntity().where(s -> s.getId().equals(id))
                .where(s -> !JinqStream.from(s.getAncestorList()).where(m -> !m.getAncestor().getDeleteKey().equals(""))
                        .exists())
                .getOnlyValue();
        return this.organizeFormatter.format(organizeEntity);
    }

}
