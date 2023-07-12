package com.springboot.project.test.service.OrganizeService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;
import com.springboot.project.model.OrganizeModel;
import com.springboot.project.test.BaseTest;

public class OrganizeServiceCreateOrganizeTest extends BaseTest {

    @Test
    public void test() {
        var organizeModel = new OrganizeModel().setName("Super Saiyan Son Goku");
        var result = this.organizeService.createOrganize(organizeModel);
        assertNotNull(result.getId());
        assertEquals(36, result.getId().length());
        assertEquals("Super Saiyan Son Goku", result.getName());
        assertEquals(0, result.getChildOrganizeList().size());
        assertNull(result.getParentOrganize());
    }

}
