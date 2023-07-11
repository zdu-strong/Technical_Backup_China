package com.springboot.project.test.service.OrganizeService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.springboot.project.model.OrganizeModel;
import com.springboot.project.test.BaseTest;

public class OrganizeServiceFixConcurrencyMoveOrganizeTest extends BaseTest {

    private String organizeId;
    private String childOrganizeId;
    private String parentOrganizeIdOfMove;

    @Test
    public void test() {
        while (true) {
            if (!this.organizeService.fixConcurrencyMoveOrganize()) {
                break;
            }
        }
    }

    @BeforeEach
    public void beforeEach() {
        {
            var organizeModel = new OrganizeModel().setName("Super Saiyan Son Goku");
            this.organizeId = this.organizeService.createOrganize(organizeModel).getId();
            var childOrganizeModel = new OrganizeModel().setName("Son Gohan")
                    .setParentOrganize(new OrganizeModel().setId(organizeId));
            this.childOrganizeId = this.organizeService.createOrganize(childOrganizeModel).getId();
        }
        {
            var organizeModel = new OrganizeModel().setName("piccolo");
            this.parentOrganizeIdOfMove = this.organizeService.createOrganize(organizeModel).getId();
        }

        var result = this.organizeService.moveOrganize(this.childOrganizeId, this.parentOrganizeIdOfMove);
        assertNotNull(result.getId());
        assertNotEquals(this.childOrganizeId, result.getId());
        assertNotEquals(this.organizeId, result.getParentOrganize().getId());
        assertEquals("Son Gohan", result.getName());
        assertEquals(0, result.getChildOrganizeList().size());
        assertEquals(this.parentOrganizeIdOfMove, result.getParentOrganize().getId());
    }

}
