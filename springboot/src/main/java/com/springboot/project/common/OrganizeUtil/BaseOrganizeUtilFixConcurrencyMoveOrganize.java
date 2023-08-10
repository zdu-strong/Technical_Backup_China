package com.springboot.project.common.OrganizeUtil;

import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.jinq.orm.stream.JinqStream;
import org.springframework.stereotype.Service;
import com.springboot.project.entity.OrganizeClosureEntity;

@Service
public class BaseOrganizeUtilFixConcurrencyMoveOrganize extends BaseOrganizeUtilMoveOrganize {

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
                    var organizeEntity = this.createOrganizeEntity(organizeShadowEntity, parentOrganize.getLevel() + 1);
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
