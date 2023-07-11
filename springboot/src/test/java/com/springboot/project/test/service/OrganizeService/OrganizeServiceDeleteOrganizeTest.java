package com.springboot.project.test.service.OrganizeService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.springboot.project.model.OrganizeModel;
import com.springboot.project.test.BaseTest;

public class OrganizeServiceDeleteOrganizeTest extends BaseTest {

    private String organizeId;

    @Test
    public void test() {
        this.organizeService.deleteOrganize(this.organizeId);
    }

    @BeforeEach
    public void beforeEach() {
        var organizeModel = new OrganizeModel().setName("Super Saiyan Son Goku");
        this.organizeId = this.organizeService.createOrganize(organizeModel).getId();
    }

}
