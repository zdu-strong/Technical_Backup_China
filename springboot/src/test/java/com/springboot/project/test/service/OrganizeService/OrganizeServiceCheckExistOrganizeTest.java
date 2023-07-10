package com.springboot.project.test.service.OrganizeService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.springboot.project.model.OrganizeModel;
import com.springboot.project.test.BaseTest;

public class OrganizeServiceCheckExistOrganizeTest extends BaseTest {
    private String organizeId;

    @Test
    public void test() {
        this.organizeService.checkExistOrganize(this.organizeId);
    }

    @BeforeEach
    public void beforeEach() {
        var organizeModel = new OrganizeModel().setName("超级赛亚人孙悟空");
        this.organizeId = this.organizeService.createOrganize(organizeModel).getId();
    }

}
