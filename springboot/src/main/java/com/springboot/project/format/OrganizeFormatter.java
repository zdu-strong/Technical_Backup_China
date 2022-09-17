package com.springboot.project.format;

import org.springframework.stereotype.Service;
import com.springboot.project.entity.OrganizeEntity;
import com.springboot.project.model.OrganizeModel;
import com.springboot.project.service.BaseService;

@Service
public class OrganizeFormatter extends BaseService {

    public OrganizeModel format(OrganizeEntity organizeEntity) {
        var organizeId = organizeEntity.getId();
        var organizeModel = new OrganizeModel().setId(organizeEntity.getId()).setName(organizeEntity.getName());
        if (organizeEntity.getParentOrganize() != null) {
            organizeModel.setParentOrganize(new OrganizeModel().setId(organizeEntity.getParentOrganize().getId()));
        }
        var childOrganizeList = this.OrganizeEntity().where(s -> s.getParentOrganize().getId().equals(organizeId))
                .where(s -> s.getDeleteKey().equals("")).map(s -> new OrganizeModel().setId(s.getId()))
                .toList();
        organizeModel.setChildOrganizeList(childOrganizeList);
        return organizeModel;
    }
}
