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
        var organize = this.organizeUtil.moveOrganize(organizeId, targetParentOrganizeId);
        this.fixConcurrencyMoveOrganize();
        return organize;
    }

    private void fixConcurrencyMoveOrganize() {
        this.organizeUtil.fixConcurrencyMoveOrganize();
    }

}
