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
        var parentOrganize = this.OrganizeRelationshipEntity().where(s -> s.getDescendant().getId().equals(organizeId))
                .where(s -> s.getGap() == 1).select(s -> s.getAncestor()).findFirst();
        if (parentOrganize.isPresent()) {
            organizeModel.setParentOrganize(new OrganizeModel().setId(parentOrganize.get().getId()));
        }
        var childOrganizeList = this.OrganizeRelationshipEntity().where(s -> s.getAncestor().getId().equals(organizeId))
                .where(s -> s.getGap() > 0)
                .where(s -> s.getDescendant().getDeleteKey().equals(""))
                .map(s -> new OrganizeModel().setId(s.getDescendant().getId()))
                .toList();
        organizeModel.setChildOrganizeList(childOrganizeList);
        return organizeModel;
    }
}
