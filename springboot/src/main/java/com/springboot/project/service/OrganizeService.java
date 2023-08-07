package com.springboot.project.service;

import org.apache.commons.lang3.StringUtils;
import org.jinq.orm.stream.JinqStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.springboot.project.model.OrganizeModel;
import com.springboot.project.common.OrganizeUtil.OrganizeUtil;

@Component
public class OrganizeService {

    @Autowired
    private OrganizeUtil organizeUtil;

    @Autowired
    private OrganizeClosureService organizeClosureService;

    public OrganizeModel createOrganize(OrganizeModel organizeModel) {
        var organize = this.organizeUtil.createOrganizeToStart(organizeModel);
        if (organizeModel.getParentOrganize() != null
                && StringUtils.isNotBlank(organizeModel.getParentOrganize().getId())) {
            var paginationModel = this.organizeClosureService.getAncestorOfOrganizeByPagination(1L, 1L,
                    organizeModel.getParentOrganize().getId());
            for (var i = paginationModel.getTotalPage(); i > 0; i--) {
                var ancestorId = JinqStream.from(this.organizeClosureService
                        .getAncestorOfOrganizeByPagination(i, 1L, organizeModel.getParentOrganize().getId())
                        .getList())
                        .getOnlyValue();
                this.organizeClosureService.createOrganizeClosure(ancestorId, organize.getId());
            }
        }
        organize = this.organizeUtil.createOrganizeToEnd(organize.getId());
        this.fixConcurrencyMoveOrganize();
        return organize;
    }

    public void deleteOrganize(String id) {
        this.organizeUtil.deleteOrganize(id);
        this.fixConcurrencyMoveOrganize();
    }

    public OrganizeModel getOrganize(String id) {
        return this.organizeUtil.getOrganize(id);
    }

    public void checkExistOrganize(String id) {
        this.organizeUtil.checkExistOrganize(id);
    }

    public OrganizeModel moveOrganize(String organizeId, String targetParentOrganizeId) {
        var moveOrganizeMode = this.organizeUtil.moveOrganizeToStart(organizeId, targetParentOrganizeId);
        if (StringUtils.isNotBlank(moveOrganizeMode.getTargetOrganizeId())
                && StringUtils.isNotBlank(moveOrganizeMode.getTargetParentOrganizeId())) {
            var paginationModel = this.organizeClosureService.getAncestorOfOrganizeByPagination(1L, 1L,
                    moveOrganizeMode.getTargetParentOrganizeId());
            for (var i = paginationModel.getTotalPage(); i > 0; i--) {
                var ancestorId = JinqStream.from(this.organizeClosureService
                        .getAncestorOfOrganizeByPagination(i, 1L,
                                moveOrganizeMode.getTargetParentOrganizeId())
                        .getList())
                        .getOnlyValue();
                this.organizeClosureService.createOrganizeClosure(ancestorId,
                        moveOrganizeMode.getTargetOrganizeId());
            }
        }
        while (true) {
            var moveModel = this.organizeUtil.moveChildOrganizeList(moveOrganizeMode.getOrganizeId(),
                    moveOrganizeMode.getTargetOrganizeId());

            if (!moveModel.getHasNext()) {
                break;
            }
            if (StringUtils.isBlank(moveModel.getTargetOrganizeId())) {
                continue;
            }
            if (StringUtils.isBlank(moveModel.getTargetParentOrganizeId())) {
                continue;
            }
            var paginationModel = this.organizeClosureService.getAncestorOfOrganizeByPagination(1L, 1L,
                    moveModel.getTargetParentOrganizeId());
            for (var i = paginationModel.getTotalPage(); i > 0; i--) {
                var ancestorId = JinqStream.from(this.organizeClosureService
                        .getAncestorOfOrganizeByPagination(i, 1L,
                                moveModel.getTargetParentOrganizeId())
                        .getList())
                        .getOnlyValue();
                this.organizeClosureService.createOrganizeClosure(ancestorId,
                        moveModel.getTargetOrganizeId());
            }
        }
        var organize = this.organizeUtil.moveOrganizeToEnd(moveOrganizeMode.getOrganizeId(),
                moveOrganizeMode.getTargetOrganizeId(), moveOrganizeMode.getTargetParentOrganizeId());
        this.fixConcurrencyMoveOrganize();
        return organize;
    }

    public void fixConcurrencyMoveOrganize() {
        if ("".equals("")) {
            return;
        }
        while (true) {
            if (this.organizeUtil.fixConcurrencyMoveOrganizeDueToOrganizeIsDeletedAndOrganizeEntityIsAlsoDeleted()) {
                continue;
            }

            var fixConcurrencyMoveOrganizeModel = this.organizeUtil
                    .fixConcurrencyMoveOrganizeDueToOrganizeHasSubOrganizationsAndOrganizeEntityAlsoHasToStart();
            if (fixConcurrencyMoveOrganizeModel.getHasNext()) {
                if (StringUtils.isNotBlank(fixConcurrencyMoveOrganizeModel.getParentOrganizeId())
                        && StringUtils.isNotBlank(fixConcurrencyMoveOrganizeModel.getOrganizeId())) {
                    var paginationModel = this.organizeClosureService.getAncestorOfOrganizeByPagination(1L, 1L,
                            fixConcurrencyMoveOrganizeModel.getParentOrganizeId());
                    for (var i = paginationModel.getTotalPage(); i > 0; i--) {
                        var ancestorId = JinqStream.from(this.organizeClosureService
                                .getAncestorOfOrganizeByPagination(i, 1L,
                                        fixConcurrencyMoveOrganizeModel.getParentOrganizeId())
                                .getList())
                                .getOnlyValue();
                        this.organizeClosureService.createOrganizeClosure(ancestorId,
                                fixConcurrencyMoveOrganizeModel.getOrganizeId());
                    }
                    this.organizeUtil
                            .fixConcurrencyMoveOrganizeDueToOrganizeHasSubOrganizationsAndOrganizeEntityAlsoHasToEnd(
                                    fixConcurrencyMoveOrganizeModel.getOrganizeId());
                }
                continue;
            }

            if (this.organizeUtil
                    .fixConcurrencyMoveOrganizeDueToOrganizeShadowShouldOnlyHaveOneAliveOrganizeEntity()) {
                continue;
            }

            break;
        }
    }

}
