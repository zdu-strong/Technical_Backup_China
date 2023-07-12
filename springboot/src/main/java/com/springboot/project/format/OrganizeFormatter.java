package com.springboot.project.format;

import org.springframework.stereotype.Service;
import com.springboot.project.entity.OrganizeEntity;
import com.springboot.project.model.OrganizeModel;
import com.springboot.project.service.BaseService;

@Service
public class OrganizeFormatter extends BaseService {

    public OrganizeModel format(OrganizeEntity organizeEntity) {
        var organizeModel = new OrganizeModel()
                .setId(organizeEntity.getId())
                .setName(organizeEntity.getOrganizeShadow().getName())
                .setLevel(organizeEntity.getLevel());

        var id = organizeEntity.getId();

        var parentOrganize = this.OrganizeClosureEntity()
                .where(s -> s.getDescendant().getId().equals(id))
                .where(s -> s.getGap() == 1)
                .findOne()
                .map(s -> new OrganizeModel().setId(s.getAncestor().getId()))
                .orElse(null);
        organizeModel.setParentOrganize(parentOrganize);

        var childOrganizeList = this.OrganizeClosureEntity()
                .where(s -> s.getAncestor().getId().equals(id))
                .where(s -> s.getGap() == 1)
                .where(s -> !s.getDescendant().getIsDeleted())
                .map(s -> new OrganizeModel().setId(s.getDescendant().getId()))
                .toList();
        organizeModel.setChildOrganizeList(childOrganizeList);

        return organizeModel;
    }

}
