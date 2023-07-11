package com.springboot.project.test.service.OrganizeService;

import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.springboot.project.model.OrganizeModel;
import com.springboot.project.test.BaseTest;

public class OrganizeServiceGetOrganizeParentOrganizeAlreadyDeleteTest extends BaseTest {

    private String organizeId;

    @Test
    public void test() {
        assertThrows(NoSuchElementException.class, () -> {
            this.organizeService.getOrganize(organizeId);
        });
    }

    @BeforeEach
    public void beforeEach() {
        var parentOrganizeModel = new OrganizeModel().setName("Super Saiyan Son Goku");
        var parentOrganize = this.organizeService.createOrganize(parentOrganizeModel);
        var childOrganizeModel = new OrganizeModel().setName("Son Gohan").setParentOrganize(parentOrganize);
        var childOrganize = this.organizeService.createOrganize(childOrganizeModel);
        this.organizeService.deleteOrganize(parentOrganize.getId());
        this.organizeId = childOrganize.getId();
    }

}
