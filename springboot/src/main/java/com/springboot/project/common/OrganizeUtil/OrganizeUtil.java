package com.springboot.project.common.OrganizeUtil;

import java.util.Date;
import org.jinq.orm.stream.JinqStream;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.springboot.project.model.OrganizeModel;

@Service
public class OrganizeUtil extends BaseOrganizeUtilFixConcurrencyMoveOrganize {

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

    public OrganizeModel getOrganize(String id) {
        var organizeEntity = this.OrganizeEntity()
                .where(s -> s.getId().equals(id))
                .where(s -> !JinqStream.from(s.getAncestorList())
                        .where(m -> m.getAncestor().getIsDeleted())
                        .exists())
                .getOnlyValue();
        return this.organizeFormatter.format(organizeEntity);
    }

    public void checkExistOrganize(String id) {
        var isPresent = this.OrganizeEntity()
                .where(s -> s.getId().equals(id))
                .where(s -> !JinqStream.from(s.getAncestorList())
                        .where(m -> m.getAncestor().getIsDeleted())
                        .exists())
                .exists();
        if (!isPresent) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Organize does not exist");
        }
    }

}
