package com.springboot.project.common.OrganizeUtil;

import com.fasterxml.uuid.Generators;
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

        var organizeEntity = new OrganizeEntity();
        organizeEntity.setId(Generators.timeBasedGenerator().generate().toString());
        organizeEntity.setCreateDate(new Date());
        organizeEntity.setUpdateDate(new Date());
        organizeEntity.setLevel(parentOrganize == null ? 0 : parentOrganize.getLevel() + 1);
        organizeEntity.setIsDeleted(true);
        organizeEntity.setOrganizeShadow(organizeShadow);
        this.persist(organizeEntity);

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
        this.merge(organizeEntity);
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

    public MoveOrganizeMode moveOrganizeToStart(String organizeId, String targetParentOrganizeId) {
        var organizeEntity = this.OrganizeEntity().where(s -> s.getId().equals(organizeId))
                .where(s -> !JinqStream.from(s.getAncestorList())
                        .where(m -> m.getAncestor().getIsDeleted())
                        .exists())
                .getOnlyValue();
        var targetParentOrganize = StringUtils.isNotBlank(targetParentOrganizeId)
                ? this.OrganizeEntity()
                        .where(s -> s.getId().equals(targetParentOrganizeId))
                        .where(s -> !JinqStream.from(s.getAncestorList())
                                .where(m -> m.getAncestor().getIsDeleted())
                                .exists())
                        .getOnlyValue()
                : null;
        var targetOrganizeEntity = new OrganizeEntity();
        targetOrganizeEntity.setId(Generators.timeBasedGenerator().generate().toString());
        targetOrganizeEntity.setCreateDate(new Date());
        targetOrganizeEntity.setUpdateDate(new Date());
        targetOrganizeEntity.setLevel(targetParentOrganize == null ? 0 : targetParentOrganize.getLevel() + 1);
        targetOrganizeEntity.setIsDeleted(true);
        targetOrganizeEntity.setOrganizeShadow(organizeEntity.getOrganizeShadow());
        this.persist(targetOrganizeEntity);

        this.organizeClosureService.createOrganizeClosure(targetOrganizeEntity.getId(), targetOrganizeEntity.getId());

        return new MoveOrganizeMode().setOrganizeId(organizeEntity.getId())
                .setTargetOrganizeId(targetOrganizeEntity.getId())
                .setTargetParentOrganizeId(targetParentOrganizeId);
    }

    public OrganizeModel moveOrganizeToEnd(String organizeId, String targetOrganizeId, String targetParentOrganizeId) {
        var organizeEntity = this.OrganizeEntity()
                .where(s -> s.getId().equals(organizeId))
                .getOnlyValue();
        var targetOrganizeEntity = this.OrganizeEntity()
                .where(s -> s.getId().equals(targetOrganizeId))
                .getOnlyValue();
        var targetParentOrganize = StringUtils.isNotBlank(targetParentOrganizeId)
                ? this.OrganizeEntity()
                        .where(s -> s.getId().equals(targetParentOrganizeId))
                        .getOnlyValue()
                : null;

        organizeEntity.setIsDeleted(true);
        organizeEntity.setUpdateDate(new Date());
        this.merge(organizeEntity);
        targetOrganizeEntity.setIsDeleted(false);
        targetOrganizeEntity.setUpdateDate(new Date());
        targetOrganizeEntity.getOrganizeShadow()
                .setParent(targetParentOrganize != null ? targetParentOrganize.getOrganizeShadow() : null);
        targetOrganizeEntity.getOrganizeShadow().setUpdateDate(new Date());
        this.merge(targetOrganizeEntity);

        return this.organizeFormatter.format(targetOrganizeEntity);
    }

    public MoveOrganizeMode moveChildOrganizeList(String sourceOrganizeId, String targetOrganizeId) {
        var levelOfSourceOrganize = this.OrganizeEntity()
                .where(s -> s.getId().equals(sourceOrganizeId))
                .select(s -> s.getLevel())
                .getOnlyValue();
        var levelTargetOrganize = this.OrganizeEntity()
                .where(s -> s.getId().equals(targetOrganizeId))
                .select(s -> s.getLevel())
                .getOnlyValue();
        var sourceChildOrganizeGroup = this.OrganizeClosureEntity()
                .where(s -> s.getGap() == 1)
                .where(s -> s.getDescendant().getLevel() > levelOfSourceOrganize)
                .where(s -> JinqStream.from(s.getDescendant().getAncestorList())
                        .where(m -> m.getAncestor().getId().equals(sourceOrganizeId))
                        .exists())
                .where(s -> !JinqStream.from(s.getDescendant().getAncestorList())
                        .where(m -> m.getAncestor().getLevel() > levelOfSourceOrganize)
                        .where(m -> m.getAncestor().getIsDeleted())
                        .exists())
                .where((s, t) -> t.stream(OrganizeClosureEntity.class)
                        .where(m -> m.getAncestor().getId().equals(targetOrganizeId))
                        .where(m -> m.getDescendant().getLevel() - levelTargetOrganize == s.getAncestor().getLevel()
                                - levelOfSourceOrganize)
                        .where(m -> m.getDescendant().getOrganizeShadow().getId()
                                .equals(s.getAncestor().getOrganizeShadow().getId()))
                        .exists())
                .where((s, t) -> !t.stream(OrganizeClosureEntity.class)
                        .where(m -> m.getAncestor().getId().equals(targetOrganizeId))
                        .where(m -> m.getDescendant().getLevel() - levelTargetOrganize == s.getDescendant().getLevel()
                                - levelOfSourceOrganize)
                        .where(m -> m.getDescendant().getOrganizeShadow().getId()
                                .equals(s.getDescendant().getOrganizeShadow().getId()))
                        .exists())
                .findFirst()
                .orElse(null);
        if (sourceChildOrganizeGroup != null) {
            var sourceParentOrganizeShadowId = sourceChildOrganizeGroup.getAncestor().getOrganizeShadow().getId();
            var levelOfSourceParentOrganize = sourceChildOrganizeGroup.getAncestor().getLevel();
            var targetParentOrganizeEntity = this.OrganizeEntity()
                    .where(s -> s.getOrganizeShadow().getId().equals(sourceParentOrganizeShadowId))
                    .where(s -> JinqStream.from(s.getAncestorList())
                            .where(m -> m.getAncestor().getId().equals(targetOrganizeId))
                            .exists())
                    .where(s -> s.getLevel() - levelTargetOrganize == levelOfSourceParentOrganize
                            - levelOfSourceOrganize)
                    .findFirst()
                    .get();
            var childTargetOrganizeEntity = new OrganizeEntity();
            childTargetOrganizeEntity.setId(Generators.timeBasedGenerator().generate().toString());
            childTargetOrganizeEntity.setCreateDate(new Date());
            childTargetOrganizeEntity.setUpdateDate(new Date());
            childTargetOrganizeEntity.setLevel(targetParentOrganizeEntity.getLevel() + 1);
            childTargetOrganizeEntity.setIsDeleted(false);
            childTargetOrganizeEntity.setOrganizeShadow(sourceChildOrganizeGroup.getDescendant().getOrganizeShadow());
            this.persist(childTargetOrganizeEntity);

            this.organizeClosureService.createOrganizeClosure(childTargetOrganizeEntity.getId(),
                    childTargetOrganizeEntity.getId());

            return new MoveOrganizeMode().setHasNext(true).setTargetOrganizeId(childTargetOrganizeEntity.getId())
                    .setTargetParentOrganizeId(targetParentOrganizeEntity.getId());
        }

        return new MoveOrganizeMode().setHasNext(false);
    }

    /**
     * has Next data need fix
     * 
     * @return
     */
    public Boolean fixConcurrencyMoveOrganizeDueToOrganizeIsDeletedAndOrganizeEntityIsAlsoDeleted() {
        // 1. OrganizeShadow is deleted, and OrganizeEntity is also deleted
        var organizeEntity = this.OrganizeEntity()
                .where(s -> s.getOrganizeShadow().getIsDeleted())
                .where(s -> !JinqStream.from(s.getAncestorList())
                        .where(m -> m.getAncestor().getIsDeleted())
                        .exists())
                .findFirst()
                .orElse(null);
        if (organizeEntity != null) {
            organizeEntity.setIsDeleted(true);
            organizeEntity.setUpdateDate(new Date());
            this.merge(organizeEntity);
            return true;
        }
        return false;
    }

    /**
     * has Next data need fix
     * 
     * @return
     */
    public FixConcurrencyMoveOrganizeModel fixConcurrencyMoveOrganizeDueToOrganizeHasSubOrganizationsAndOrganizeEntityAlsoHasToStart() {
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
                    this.persist(organizeEntity);

                    this.organizeClosureService.createOrganizeClosure(organizeEntity.getId(), organizeEntity.getId());

                    return new FixConcurrencyMoveOrganizeModel().setHasNext(true)
                            .setParentOrganizeId(parentOrganizeId)
                            .setOrganizeId(organizeEntity.getId());

                }

                return new FixConcurrencyMoveOrganizeModel().setHasNext(true);
            }
        }

        return new FixConcurrencyMoveOrganizeModel().setHasNext(false);
    }

    /**
     * 
     * @return
     */
    public void fixConcurrencyMoveOrganizeDueToOrganizeHasSubOrganizationsAndOrganizeEntityAlsoHasToEnd(
            String organizeId) {
        var organizeEntity = this.OrganizeEntity()
                .where(s -> s.getId().equals(organizeId))
                .getOnlyValue();
        organizeEntity.setIsDeleted(false);
        organizeEntity.setUpdateDate(new Date());
        this.merge(organizeEntity);
    }

    /**
     * has Next data need fix
     * 
     * @return
     */
    public Boolean fixConcurrencyMoveOrganizeDueToOrganizeShadowShouldOnlyHaveOneAliveOrganizeEntity() {
        // 3. An OrganizeShadow should only have one alive OrganizeEntity
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
                this.merge(organizeEntity);
            }

            return true;
        }

        return false;
    }

}
