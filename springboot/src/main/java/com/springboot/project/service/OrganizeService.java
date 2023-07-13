package com.springboot.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.springboot.project.model.OrganizeModel;
import com.springboot.project.common.OrganizeUtil.OrganizeUtil;

@Component
public class OrganizeService {

    @Autowired
    private OrganizeUtil organizeUtil;

    public OrganizeModel createOrganize(OrganizeModel organizeModel) {
        var organize = this.organizeUtil.createOrganize(organizeModel);
        this.organizeUtil.fixConcurrencyMoveOrganize();
        return organize;
    }

    public void deleteOrganize(String id) {
        this.organizeUtil.deleteOrganize(id);
        this.organizeUtil.fixConcurrencyMoveOrganize();
    }

    public OrganizeModel getOrganize(String id) {
        return this.organizeUtil.getOrganize(id);
    }

    public void checkExistOrganize(String id) {
        this.organizeUtil.checkExistOrganize(id);
    }

    public OrganizeModel moveOrganize(String organizeId, String targetParentOrganizeId) {
        var organize = this.organizeUtil.moveOrganize(organizeId, targetParentOrganizeId);
        this.organizeUtil.fixConcurrencyMoveOrganize();
        return organize;
    }

}
