package com.springboot.project.common.OrganizeUtil;

import org.jinq.orm.stream.JinqStream;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.springboot.project.model.OrganizeModel;

@Service
public class OrganizeUtil extends BaseOrganizeUtilDeleteOrganize {

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
