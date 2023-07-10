package com.springboot.project.format;

import org.springframework.stereotype.Service;
import com.springboot.project.entity.OrganizeEntity;
import com.springboot.project.model.OrganizeModel;
import com.springboot.project.service.BaseService;

@Service
public class OrganizeFormatter extends BaseService {

    public OrganizeModel format(OrganizeEntity organizeEntity) {
        var levelOfOrganize = organizeEntity.getLevel();
        var organizePath = organizeEntity.getPath();
        var organizeModel = new OrganizeModel().setId(organizeEntity.getId())
                .setName(organizeEntity.getOrganizeShadow().getName());

        organizeModel.setParentOrganize(this.OrganizeEntity()
                .where(s -> organizePath.contains(s.getPath()))
                .where(s -> levelOfOrganize - s.getLevel() == 1)
                .map(s -> new OrganizeModel().setId(s.getId())).findFirst().orElse(null));

        var childOrganizeList = this.OrganizeEntity()
                .where(s -> s.getPath().contains(organizePath))
                .where(s -> s.getLevel() - levelOfOrganize == 1)
                .map(s -> new OrganizeModel().setId(s.getId()))
                .toList();
        organizeModel.setChildOrganizeList(childOrganizeList);
        return organizeModel;
    }
}
