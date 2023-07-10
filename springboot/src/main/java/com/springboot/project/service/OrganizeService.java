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

    public OrganizeModel moveOrganize(String organizeId, String parentOrganizeId) {
        var originOrganize = this.OrganizeEntity().where(s -> s.getId().equals(organizeId))
                .where((s, t) -> !t.stream(OrganizeEntity.class).where(m -> s.getPath().contains(m.getPath()))
                        .where(m -> !m.getDeleteKey().equals(""))
                        .exists())
                .getOnlyValue();
        var originOrganizeLevel = originOrganize.getLevel();
        var parentOrganize = StringUtils.isNotBlank(parentOrganizeId)
                ? this.OrganizeEntity().where(s -> s.getId().equals(parentOrganizeId))
                        .where((s, t) -> !t.stream(OrganizeEntity.class).where(m -> s.getPath().contains(m.getPath()))
                                .where(m -> !m.getDeleteKey().equals(""))
                                .exists())
                        .getOnlyValue()
                : null;
        var organizeEntity = new OrganizeEntity();
        organizeEntity.setId(Generators.timeBasedGenerator().generate().toString());
        organizeEntity.setDeleteKey(Generators.timeBasedGenerator().generate().toString());
        organizeEntity.setOrganizeShadow(originOrganize.getOrganizeShadow());
        organizeEntity.setPath(
                parentOrganize == null ? organizeEntity.getId() + ";"
                        : parentOrganize.getPath() + organizeEntity.getId() + ";");
        organizeEntity.setLevel(parentOrganize == null ? 0 : parentOrganize.getLevel() + 1);
        this.entityManager.persist(organizeEntity);

        var originChildIdList = this.OrganizeEntity()
                .where(s -> s.getPath().contains(organizeId))
                .where(s -> s.getLevel() - originOrganizeLevel == 1)
                .where(s -> s.getDeleteKey().equals(""))
                .select(s -> s.getId()).toList();
        for (var originChildId : originChildIdList) {
            this.moveChildOrganize(originChildId, organizeEntity.getId());
        }

        organizeEntity.setDeleteKey("");
        this.entityManager.merge(organizeEntity);
        originOrganize.setDeleteKey(Generators.timeBasedGenerator().generate().toString());
        this.entityManager.merge(originOrganize);

        return this.organizeFormatter.format(organizeEntity);
    }

    public void moveChildOrganize(String organizeId, String parentOrganizeId) {
        var originOrganize = this.OrganizeEntity().where(s -> s.getId().equals(organizeId))
                .getOnlyValue();
        var originOrganizeLevel = originOrganize.getLevel();
        var parentOrganize = StringUtils.isNotBlank(parentOrganizeId)
                ? this.OrganizeEntity().where(s -> s.getId().equals(parentOrganizeId))
                        .getOnlyValue()
                : null;
        var organizeEntity = new OrganizeEntity();
        organizeEntity.setId(Generators.timeBasedGenerator().generate().toString());
        organizeEntity.setDeleteKey("");
        organizeEntity.setOrganizeShadow(originOrganize.getOrganizeShadow());
        organizeEntity.setPath(
                parentOrganize == null ? organizeEntity.getId() + ";"
                        : parentOrganize.getPath() + organizeEntity.getId() + ";");
        organizeEntity.setLevel(parentOrganize == null ? 0 : parentOrganize.getLevel() + 1);
        this.entityManager.persist(organizeEntity);

        var originChildIdList = this.OrganizeEntity()
                .where(s -> s.getPath().contains(organizeId))
                .where(s -> s.getLevel() - originOrganizeLevel == 1)
                .where(s -> s.getDeleteKey().equals(""))
                .select(s -> s.getId()).toList();
        for (var originChildId : originChildIdList) {
            this.moveChildOrganize(originChildId, organizeEntity.getId());
        }
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
