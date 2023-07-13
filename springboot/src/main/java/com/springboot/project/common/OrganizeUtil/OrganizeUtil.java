package com.springboot.project.common.OrganizeUtil;

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
import com.springboot.project.service.BaseService;
import com.springboot.project.service.OrganizeClosureService;
import com.springboot.project.service.OrganizeShadowService;
import com.springboot.project.entity.*;

@Service
public class OrganizeUtil extends BaseService {

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
                                .where(m -> m.getAncestor().getIsDeleted()).exists())
                        .getOnlyValue()
                : null;

        var organizeEntity = new OrganizeEntity();
        organizeEntity.setId(Generators.timeBasedGenerator().generate().toString());
        organizeEntity.setCreateDate(new Date());
        organizeEntity.setUpdateDate(new Date());
        organizeEntity.setLevel(parentOrganize == null ? 0 : parentOrganize.getLevel() + 1);
        organizeEntity.setIsDeleted(true);
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

        organizeEntity.setIsDeleted(false);
        organizeEntity.setUpdateDate(new Date());
        organizeEntity.getOrganizeShadow().setIsDeleted(false);
        organizeEntity.getOrganizeShadow().setUpdateDate(new Date());
        this.entityManager.merge(organizeEntity);

        return this.organizeFormatter.format(organizeEntity);
    }

    public void deleteOrganize(String id) {
        var organizeEntity = this.OrganizeEntity()
                .where(s -> s.getId().equals(id))
                .where(s -> !JinqStream.from(s.getAncestorList())
                        .where(m -> m.getAncestor().getIsDeleted())
                        .exists())
                .getOnlyValue();
        organizeEntity.getOrganizeShadow().setIsDeleted(true);
        organizeEntity.getOrganizeShadow().setUpdateDate(new Date());
        organizeEntity.setIsDeleted(true);
        organizeEntity.setUpdateDate(new Date());
        this.entityManager.merge(organizeEntity);
    }

    public OrganizeModel getOrganize(String id) {
        var organizeEntity = this.OrganizeEntity()
                .where(s -> s.getId().equals(id))
                .where(s -> !JinqStream.from(s.getAncestorList())
                        .where(m -> m.getAncestor().getIsDeleted())
                        .exists())
                .getOnlyValue();
        return this.organizeFormatter.format(organizeEntity);
    }

    public void checkExistOrganize(String id) {
        var isPresent = this.OrganizeEntity()
                .where(s -> s.getId().equals(id))
                .where(s -> !JinqStream.from(s.getAncestorList())
                        .where(m -> m.getAncestor().getIsDeleted())
                        .exists())
                .exists();
        if (!isPresent) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Organize does not exist");
        }
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
                .where(s -> !s.getDescendant().getIsDeleted())
                .map(s -> s.getDescendant().getId())
                .toList();

        for (var sourceChildOrganizeId : sourceChildOrganizeIdList) {
            var targetParentOrganize = this.OrganizeEntity().where(s -> s.getId().equals(targetOrganizeId))
                    .getOnlyValue();
            var sourceChildOrganizeEntity = this.OrganizeEntity().where(s -> s.getId().equals(sourceChildOrganizeId))
                    .getOnlyValue();

            var childTargetOrganizeEntity = new OrganizeEntity();
            childTargetOrganizeEntity.setId(Generators.timeBasedGenerator().generate().toString());
            childTargetOrganizeEntity.setCreateDate(new Date());
            childTargetOrganizeEntity.setUpdateDate(new Date());
            childTargetOrganizeEntity.setLevel(targetParentOrganize.getLevel() + 1);
            childTargetOrganizeEntity.setIsDeleted(false);
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
                            .where(m -> m.getAncestor().getIsDeleted())
                            .exists())
                    .where(s -> s.getOrganizeShadow().getIsDeleted())
                    .findFirst()
                    .orElse(null);
            if (organizeEntity != null) {
                organizeEntity.setIsDeleted(true);
                organizeEntity.setUpdateDate(new Date());
                this.entityManager.merge(organizeEntity);
                return true;
            }
        }

        // 2. OrganizeShadow has sub-organizations, and OrganizeEntity also has
        {
            var parentOrganize = this.OrganizeEntity()
                    .where(s -> !JinqStream.from(s.getAncestorList())
                            .where(m -> m.getAncestor().getIsDeleted())
                            .exists())
                    .where((s) -> JinqStream.from(s.getDescendantList())
                            .where(m -> m.getGap() == 1)
                            .where(m -> !m.getDescendant().getIsDeleted())
                            .count() < JinqStream.from(s.getOrganizeShadow().getChildList())
                                    .where(m -> !m.getIsDeleted())
                                    .count())
                    .findFirst()
                    .orElse(null);
            if (parentOrganize != null) {
                var parentOrganizeId = parentOrganize.getId();
                var parentOrganizeShadowId = parentOrganize.getOrganizeShadow().getId();
                var organizeShadowEntity = this.OrganizeShadowEntity()
                        .where(s -> s.getParent().getId().equals(parentOrganizeShadowId))
                        .where(s -> !s.getIsDeleted())
                        .where((s, t) -> !t.stream(OrganizeClosureEntity.class)
                                .where(m -> m.getAncestor().getId().equals(parentOrganizeId))
                                .where(m -> m.getGap() == 1)
                                .where(m -> !m.getDescendant().getIsDeleted())
                                .where(m -> m.getDescendant().getOrganizeShadow().getId().equals(s.getId()))
                                .exists())
                        .findFirst()
                        .orElse(null);
                if (organizeShadowEntity != null) {
                    var organizeEntity = new OrganizeEntity();
                    organizeEntity.setId(Generators.timeBasedGenerator().generate().toString());
                    organizeEntity.setCreateDate(new Date());
                    organizeEntity.setUpdateDate(new Date());
                    organizeEntity.setLevel(parentOrganize.getLevel() + 1);
                    organizeEntity.setIsDeleted(true);
                    organizeEntity.setOrganizeShadow(organizeShadowEntity);
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

                    organizeEntity.setIsDeleted(false);
                    organizeEntity.setUpdateDate(new Date());
                    this.entityManager.merge(organizeEntity);
                }

                return true;
            }
        }

        // 3. An OrganizeShadow should only have one alive OrganizeEntity
        {
            var organizeShadowId = this.OrganizeEntity()
                    .where(s -> !JinqStream.from(s.getAncestorList())
                            .where(m -> m.getAncestor().getIsDeleted())
                            .exists())
                    .group((s) -> s.getOrganizeShadow().getId(), (s, t) -> t.count())
                    .where(s -> s.getTwo() > 1)
                    .select(s -> s.getOne())
                    .findFirst()
                    .orElse(null);
            if (StringUtils.isNotBlank(organizeShadowId)) {
                var organizeList = this.OrganizeEntity()
                        .where(s -> !JinqStream.from(s.getAncestorList())
                                .where(m -> m.getAncestor().getIsDeleted())
                                .exists())
                        .where(s -> s.getOrganizeShadow().getId().equals(organizeShadowId))
                        .limit(2)
                        .toList();
                if (organizeList.size() == 2) {
                    var organizeEntity = JinqStream.from(organizeList)
                            .sortedDescendingBy(s -> s.getId())
                            .sortedDescendingBy(s -> s.getCreateDate())
                            .sortedBy(s -> {
                                var organizeModel = this.organizeFormatter.format(s);
                                if (s.getOrganizeShadow().getParent() == null) {
                                    return organizeModel.getParentOrganize() == null;
                                } else if (organizeModel.getParentOrganize() == null) {
                                    return false;
                                } else {
                                    return organizeModel.getParentOrganize().getId()
                                            .equals(s.getOrganizeShadow().getParent().getId());
                                }
                            })
                            .findFirst()
                            .get();
                    organizeEntity.setIsDeleted(true);
                    organizeEntity.setUpdateDate(new Date());
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
                ? this.OrganizeEntity()
                        .where(s -> s.getId().equals(targetParentOrganizeId))
                        .getOnlyValue()
                : null;
        var targetOrganizeEntity = new OrganizeEntity();
        targetOrganizeEntity.setId(Generators.timeBasedGenerator().generate().toString());
        targetOrganizeEntity.setCreateDate(new Date());
        targetOrganizeEntity.setUpdateDate(new Date());
        targetOrganizeEntity.setLevel(targetParentOrganize == null ? 0 : targetParentOrganize.getLevel() + 1);
        targetOrganizeEntity.setIsDeleted(true);
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

        organizeEntity.setIsDeleted(true);
        organizeEntity.setUpdateDate(new Date());
        this.entityManager.merge(organizeEntity);
        targetOrganizeEntity.setIsDeleted(false);
        targetOrganizeEntity.setUpdateDate(new Date());
        targetOrganizeEntity.getOrganizeShadow()
                .setParent(targetParentOrganize != null ? targetParentOrganize.getOrganizeShadow() : null);
        targetOrganizeEntity.getOrganizeShadow().setUpdateDate(new Date());
        this.entityManager.merge(targetOrganizeEntity);

        return this.organizeFormatter.format(targetOrganizeEntity);
    }

}
