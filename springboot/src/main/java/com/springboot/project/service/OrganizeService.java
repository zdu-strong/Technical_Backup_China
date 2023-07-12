package com.springboot.project.service;

import com.fasterxml.uuid.Generators;
import com.google.common.collect.Lists;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.jinq.orm.stream.JinqStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
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
        organizeEntity.setDeleteKey(Generators.timeBasedGenerator().generate().toString());
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

        organizeEntity.setDeleteKey("");
        organizeEntity.getOrganizeShadow().setDeleteKey("");
        this.entityManager.merge(organizeEntity);

        return this.organizeFormatter.format(organizeEntity);
    }

    public OrganizeModel moveOrganize(String organizeId, String targetParentOrganizeId) {
        this.checkExistOrganize(organizeId);
        if (StringUtils.isNotBlank(targetParentOrganizeId)) {
            this.checkExistOrganize(targetParentOrganizeId);
        }

        return this.moveOrganizeNotCheck(organizeId, targetParentOrganizeId);
    }

    private void moveChildOrganizeList(String sourceOrganizeId, String targetOrganizeId) {
        var sourceChildOrganizeIdList = this.OrganizeClosureEntity()
                .where(s -> s.getAncestor().getId().equals(sourceOrganizeId))
                .where(s -> s.getGap() == 1)
                .where(s -> s.getDescendant().getDeleteKey().equals(""))
                .map(s -> s.getDescendant().getId())
                .toList();

        for (var sourceChildOrganizeId : sourceChildOrganizeIdList) {
            var targetParentOrganize = this.OrganizeEntity().where(s -> s.getId().equals(targetOrganizeId))
                    .getOnlyValue();
            var sourceChildOrganizeEntity = this.OrganizeEntity().where(s -> s.getId().equals(sourceChildOrganizeId))
                    .getOnlyValue();

            var childTargetOrganizeEntity = new OrganizeEntity();
            childTargetOrganizeEntity.setId(Generators.timeBasedGenerator().generate().toString());
            childTargetOrganizeEntity.setLevel(targetParentOrganize.getLevel() + 1);
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
        organizeEntity.getOrganizeShadow().setDeleteKey(Generators.timeBasedGenerator().generate().toString());
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

    public void checkExistOrganize(String id) {
        var isPresent = this.OrganizeEntity().where(s -> s.getId().equals(id))
                .where(s -> !JinqStream.from(s.getAncestorList()).where(m -> !m.getAncestor().getDeleteKey().equals(""))
                        .exists())
                .exists();
        if (!isPresent) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Organize does not exist");
        }
    }

    /**
     * has Next data need fix
     * 
     * @return
     */
    public Boolean fixConcurrencyMoveOrganize() {
        // 1. OrganizeShadow is deleted, and OrganizeEntity is also deleted
        {
            var organizeEntity = this.OrganizeEntity()
                    .where(s -> !JinqStream.from(s.getAncestorList())
                            .where(m -> !m.getAncestor().getDeleteKey().equals(""))
                            .exists())
                    .where(s -> !s.getOrganizeShadow().getDeleteKey().equals(""))
                    .findFirst()
                    .orElse(null);
            if (organizeEntity != null) {
                organizeEntity.setDeleteKey(Generators.timeBasedGenerator().generate().toString());
                this.entityManager.merge(organizeEntity);
                return true;
            }
        }

        // 2. OrganizeShadow has sub-organizations, and OrganizeEntity also has
        {
            var organizeGroup = this.OrganizeEntity().join((s, t) -> t.stream(OrganizeEntity.class))
                    .where(s -> !JinqStream.from(s.getTwo().getAncestorList())
                            .where(m -> !m.getAncestor().getDeleteKey().equals(""))
                            .exists())
                    .where(s -> s.getOne().getDeleteKey().equals(""))
                    .where(s -> JinqStream.from(s.getTwo().getOrganizeShadow().getChildList())
                            .where(m -> m.getDeleteKey().equals(""))
                            .where(m -> m.getId().equals(s.getOne().getOrganizeShadow().getId()))
                            .exists())
                    .where(s -> !JinqStream.from(s.getTwo().getDescendantList())
                            .where(m -> m.getGap() == 1)
                            .where(m -> m.getDescendant().getDeleteKey().equals(""))
                            .where(m -> m.getDescendant().getOrganizeShadow().getId()
                                    .equals(s.getOne().getOrganizeShadow().getId()))
                            .exists())
                    .findFirst()
                    .orElse(null);
            if (organizeGroup != null) {
                var childOrganize = organizeGroup.getOne();
                var parentOrganize = organizeGroup.getTwo();
                this.moveOrganizeNotCheck(childOrganize.getId(), parentOrganize.getId());
                return true;
            }
        }

        // 3. An OrganizeShadow should only have one alive OrganizeEntity
        {
            var organizeShadowId = this.OrganizeEntity()
                    .where(s -> !JinqStream.from(s.getAncestorList())
                            .where(m -> !m.getAncestor().getDeleteKey().equals(""))
                            .exists())
                    .group((s) -> s.getOrganizeShadow().getId(), (s, t) -> t.count())
                    .where(s -> s.getTwo() > 1)
                    .select(s -> s.getOne())
                    .findFirst().orElse(null);
            if (StringUtils.isNotBlank(organizeShadowId)) {
                var organizeList = this.OrganizeEntity()
                        .where(s -> !JinqStream.from(s.getAncestorList())
                                .where(m -> !m.getAncestor().getDeleteKey().equals(""))
                                .exists())
                        .where(s -> s.getOrganizeShadow().getId().equals(organizeShadowId))
                        .limit(2)
                        .toList();
                if (organizeList.size() == 2) {
                    var organizeEntity = JinqStream.from(organizeList)
                            .sortedDescendingBy(s -> s.getId())
                            .sortedBy(s -> {
                                var organizeModel = this.organizeFormatter.format(s);
                                if (s.getOrganizeShadow().getParent() == null) {
                                    return organizeModel.getParentOrganize() == null;
                                } else if (organizeModel.getParentOrganize() == null) {
                                    return false;
                                } else {
                                    return organizeModel.getParentOrganize().getId()
                                            .equals(s.getOrganizeShadow().getId());
                                }
                            })
                            .findFirst()
                            .get();
                    organizeEntity.setDeleteKey(Generators.timeBasedGenerator().generate().toString());
                    this.entityManager.merge(organizeEntity);
                }

                return true;
            }
        }

        return false;
    }

    private OrganizeModel moveOrganizeNotCheck(String organizeId, String targetParentOrganizeId) {
        var organizeEntity = this.OrganizeEntity().where(s -> s.getId().equals(organizeId))
                .getOnlyValue();
        var targetParentOrganize = StringUtils.isNotBlank(targetParentOrganizeId)
                ? this.OrganizeEntity().where(s -> s.getId().equals(targetParentOrganizeId))
                        .where(s -> !JinqStream.from(s.getAncestorList())
                                .where(m -> !m.getAncestor().getDeleteKey().equals(""))
                                .exists())
                        .getOnlyValue()
                : null;
        var targetOrganizeEntity = new OrganizeEntity();
        targetOrganizeEntity.setId(Generators.timeBasedGenerator().generate().toString());
        targetOrganizeEntity.setLevel(targetParentOrganize == null ? 0 : targetParentOrganize.getLevel() + 1);
        targetOrganizeEntity.setDeleteKey(Generators.timeBasedGenerator().generate().toString());
        targetOrganizeEntity.setOrganizeShadow(organizeEntity.getOrganizeShadow());
        targetOrganizeEntity.setAncestorList(Lists.newArrayList());
        targetOrganizeEntity.setDescendantList(Lists.newArrayList());
        this.entityManager.persist(organizeEntity);

        this.organizeClosureService.createOrganizeClosure(targetOrganizeEntity.getId(), targetOrganizeEntity.getId());

        if (targetParentOrganize != null) {
            var ancestorIdList = this.OrganizeClosureEntity()
                    .where(s -> s.getDescendant().getId().equals(targetParentOrganizeId))
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
        targetOrganizeEntity.getOrganizeShadow()
                .setParent(targetParentOrganize != null ? targetParentOrganize.getOrganizeShadow() : null);
        this.entityManager.merge(targetOrganizeEntity);

        return this.organizeFormatter.format(targetOrganizeEntity);
    }

}
