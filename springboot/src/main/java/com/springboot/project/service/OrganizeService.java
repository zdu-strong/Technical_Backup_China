package com.springboot.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.springboot.project.model.OrganizeModel;
import com.springboot.project.common.OrganizeUtil.OrganizeUtil;

@Component
public class OrganizeService extends BaseService {

    @Autowired
    private OrganizeUtil organizeUtil;

    public OrganizeModel createOrganize(OrganizeModel organizeModel) {
        return this.organizeUtil.createOrganize(organizeModel);
    }

    public void deleteOrganize(String id) {
        this.organizeUtil.deleteOrganize(id);
    }

    public OrganizeModel getOrganize(String id) {
        return this.organizeUtil.getOrganize(id);
    }

    public void checkExistOrganize(String id) {
        this.organizeUtil.checkExistOrganize(id);
    }

    public OrganizeModel moveOrganize(String organizeId, String targetParentOrganizeId) {
        return this.organizeUtil.moveOrganize(organizeId, targetParentOrganizeId);
    }

}
