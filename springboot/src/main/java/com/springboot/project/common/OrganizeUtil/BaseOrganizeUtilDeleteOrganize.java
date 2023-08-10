package com.springboot.project.common.OrganizeUtil;

import java.util.Date;
import org.jinq.orm.stream.JinqStream;
import org.springframework.stereotype.Service;

@Service
public class BaseOrganizeUtilDeleteOrganize extends BaseOrganizeUtilFixConcurrencyMoveOrganize {

    public void deleteOrganize(String id) {
        var organizeEntity = this.OrganizeEntity()
                .where(s -> s.getId().equals(id))
                .where(s -> !JinqStream.from(s.getAncestorList())
                        .where(m -> m.getAncestor().getIsDeleted())
                        .exists())
                .getOnlyValue();
        organizeEntity.getOrganizeShadow().setIsDeleted(true);
        organizeEntity.getOrganizeShadow().setUpdateDate(new Date());
        organizeEntity.setIsDeleted(true);
        organizeEntity.setUpdateDate(new Date());
        this.merge(organizeEntity);
    }

}
