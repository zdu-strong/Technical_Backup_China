package com.springboot.project.test.service.OrganizeService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.springboot.project.model.OrganizeModel;
import com.springboot.project.test.BaseTest;

public class OrganizeServiceMoveOrganizeTest extends BaseTest {

    private String organizeId;
    private String childOrganizeId;
    private String parentOrganizeIdOfMove;

    @Test
    public void test() {
        var result = this.organizeService.moveOrganize(this.childOrganizeId, this.parentOrganizeIdOfMove);
        assertNotNull(result.getId());
        assertNotEquals(this.childOrganizeId, result.getId());
        assertNotEquals(this.organizeId, result.getParentOrganize().getId());
        assertEquals("孙悟饭", result.getName());
        assertEquals(0, result.getChildOrganizeList().size());
        assertEquals(this.parentOrganizeIdOfMove, result.getParentOrganize().getId());
    }

    @BeforeEach
    public void beforeEach() {
        {
            var organizeModel = new OrganizeModel().setName("超级赛亚人孙悟空");
            this.organizeId = this.organizeService.createOrganize(organizeModel).getId();
            var childOrganizeModel = new OrganizeModel().setName("孙悟饭")
                    .setParentOrganize(new OrganizeModel().setId(organizeId));
            this.childOrganizeId = this.organizeService.createOrganize(childOrganizeModel).getId();
        }
        {
            var organizeModel = new OrganizeModel().setName("比克");
            this.parentOrganizeIdOfMove = this.organizeService.createOrganize(organizeModel).getId();
        }
    }

}
