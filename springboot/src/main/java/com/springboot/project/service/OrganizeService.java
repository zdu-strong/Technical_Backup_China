package com.springboot.project.service;

import com.fasterxml.uuid.Generators;
import com.google.common.collect.Lists;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import com.springboot.project.model.OrganizeModel;
import com.springboot.project.entity.*;

@Service
public class OrganizeService extends BaseService {

    public OrganizeModel createOrganize(OrganizeModel organizeModel) {
        var parentOrganizeId = organizeModel.getParentOrganize() != null
                ? organizeModel.getParentOrganize().getId()
                : null;
        var parentOrganize = StringUtils.isNotBlank(parentOrganizeId)
                ? this.OrganizeEntity().where(s -> s.getId().equals(parentOrganizeId))
                        .where((s, t) -> !t.stream(OrganizeEntity.class).where(m -> s.getPath().contains(m.getPath()))
                                .where(m -> !m.getDeleteKey().equals(""))
                                .exists())
                        .getOnlyValue()
                : null;

        var organizeShadowEntity = this.createOrganizeShadow(organizeModel);

        var organize = new OrganizeEntity();
        organize.setId(Generators.timeBasedGenerator().generate().toString());
        organize.setDeleteKey("");
        organize.setOrganizeShadow(organizeShadowEntity);
        organize.setPath(
                parentOrganize == null ? organize.getId() + ";" : parentOrganize.getPath() + organize.getId() + ";");
        organize.setLevel(parentOrganize == null ? 0 : parentOrganize.getLevel() + 1);
        this.entityManager.persist(organize);

        return this.organizeFormatter.format(organize);
    }

    public void deleteOrganize(String id) {
        var organize = this.OrganizeEntity().where(s -> s.getId().equals(id))
                .where((s, t) -> !t.stream(OrganizeEntity.class).where(m -> s.getPath().contains(m.getPath()))
                        .where(m -> !m.getDeleteKey().equals(""))
                        .exists())
                .getOnlyValue();
        organize.getOrganizeShadow().setUpdateDate(new Date());
        organize.setDeleteKey(Generators.timeBasedGenerator().generate().toString());
        this.entityManager.merge(organize);
    }

    public OrganizeModel getOrganize(String id) {
        var organize = this.OrganizeEntity().where(s -> s.getId().equals(id))
                .where((s, t) -> !t.stream(OrganizeEntity.class).where(m -> s.getPath().contains(m.getPath()))
                        .where(m -> !m.getDeleteKey().equals(""))
                        .exists())
                .getOnlyValue();
        return this.organizeFormatter.format(organize);
    }

    public Boolean isChildOrganize(String childOrganizeId, String parentOrganizeId) {
        return this.OrganizeEntity().where(s -> s.getId().equals(childOrganizeId))
                .where((s, t) -> !t.stream(OrganizeEntity.class).where(m -> s.getPath().contains(m.getPath()))
                        .where(m -> !m.getDeleteKey().equals("")).exists())
                .where(s -> s.getPath().contains(parentOrganizeId))
                .exists();
    }

    private OrganizeShadowEntity createOrganizeShadow(OrganizeModel organizeModel) {
        var organizeShadowEntity = new OrganizeShadowEntity();
        organizeShadowEntity.setId(Generators.timeBasedGenerator().generate().toString());
        organizeShadowEntity.setName(organizeModel.getName());
        organizeShadowEntity.setCreateDate(new Date());
        organizeShadowEntity.setUpdateDate(new Date());
        organizeShadowEntity.setOrganizeList(Lists.newArrayList());
        this.entityManager.persist(organizeShadowEntity);

        return organizeShadowEntity;
    }
}
