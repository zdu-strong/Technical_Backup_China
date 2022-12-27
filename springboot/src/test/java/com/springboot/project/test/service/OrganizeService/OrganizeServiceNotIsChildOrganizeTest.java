package com.springboot.project.test.service.OrganizeService;

import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.springboot.project.model.OrganizeModel;
import com.springboot.project.test.BaseTest;

public class OrganizeServiceNotIsChildOrganizeTest extends BaseTest {
    private OrganizeModel parentOrganize;
    private OrganizeModel childOrganize;

    @Test
    public void test() {
        var result = this.organizeService.isChildOrganize(this.parentOrganize.getId(), this.childOrganize.getId());
        assertFalse(result);
    }

    @BeforeEach
    public void beforeEach() {
        var organizeModel = new OrganizeModel().setName("超级赛亚人孙悟空");
        this.parentOrganize = this.organizeService.createOrganize(organizeModel);
        var childOrganizeModel = new OrganizeModel().setName("孙悟饭").setParentOrganize(this.parentOrganize);
        this.childOrganize = this.organizeService.createOrganize(childOrganizeModel);
    }

}
