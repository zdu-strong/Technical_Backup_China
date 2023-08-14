package com.springboot.project.common.OrganizeUtil;

import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.jinq.orm.stream.JinqStream;
import org.springframework.stereotype.Service;
import com.springboot.project.entity.OrganizeClosureEntity;
import com.springboot.project.model.MoveOrganizeResultModel;
import com.springboot.project.model.OrganizeModel;

@Service
public class BaseOrganizeUtilMoveOrganize extends BaseOrganizeUtilCreateOrganize {

    public MoveOrganizeResultModel moveOrganizeToStart(String organizeId, String targetParentOrganizeId) {
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
        var targetOrganizeEntity = this.createOrganizeEntity(organizeEntity.getOrganizeShadow(),
                targetParentOrganize == null ? 0 : targetParentOrganize.getLevel() + 1);

        this.organizeClosureService.createOrganizeClosure(targetOrganizeEntity.getId(), targetOrganizeEntity.getId());

        return new MoveOrganizeResultModel().setOrganizeId(organizeEntity.getId())
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

    public MoveOrganizeResultModel moveChildOrganizeList(String sourceOrganizeId, String targetOrganizeId) {
        var levelOfSourceOrganize = this.OrganizeEntity()
                .where(s -> s.getId().equals(sourceOrganizeId))
                .select(s -> s.getLevel())
                .getOnlyValue();
        var levelTargetOrganize = this.OrganizeEntity()
                .where(s -> s.getId().equals(targetOrganizeId))
                .select(s -> s.getLevel())
                .getOnlyValue();
        var sourceChildOrganizeAndSourceParentOrganizeClosureGroup = this.OrganizeClosureEntity()
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
        if (sourceChildOrganizeAndSourceParentOrganizeClosureGroup != null) {
            var sourceParentOrganizeShadowId = sourceChildOrganizeAndSourceParentOrganizeClosureGroup.getAncestor().getOrganizeShadow().getId();
            var levelOfSourceParentOrganize = sourceChildOrganizeAndSourceParentOrganizeClosureGroup.getAncestor().getLevel();
            var targetParentOrganizeEntity = this.OrganizeEntity()
                    .where(s -> s.getOrganizeShadow().getId().equals(sourceParentOrganizeShadowId))
                    .where(s -> JinqStream.from(s.getAncestorList())
                            .where(m -> m.getAncestor().getId().equals(targetOrganizeId))
                            .exists())
                    .where(s -> s.getLevel() - levelTargetOrganize == levelOfSourceParentOrganize
                            - levelOfSourceOrganize)
                    .findFirst()
                    .get();

            var childTargetOrganizeEntity = this.createOrganizeEntity(
                    sourceChildOrganizeAndSourceParentOrganizeClosureGroup.getDescendant().getOrganizeShadow(),
                    targetParentOrganizeEntity.getLevel() + 1);
            childTargetOrganizeEntity.setIsDeleted(false);
            this.merge(childTargetOrganizeEntity);

            this.organizeClosureService.createOrganizeClosure(childTargetOrganizeEntity.getId(),
                    childTargetOrganizeEntity.getId());

            return new MoveOrganizeResultModel().setHasNext(true).setTargetOrganizeId(childTargetOrganizeEntity.getId())
                    .setTargetParentOrganizeId(targetParentOrganizeEntity.getId());
        }

        return new MoveOrganizeResultModel().setHasNext(false);
    }

}
