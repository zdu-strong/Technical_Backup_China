package com.springboot.project.test.controller.OrganizeController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.net.URISyntaxException;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import com.springboot.project.model.OrganizeModel;
import com.springboot.project.test.BaseTest;

public class OrganizeControllerGetOrganizeTest extends BaseTest {

    private String organizeId;

    @Test
    public void test() throws URISyntaxException {
        var url = new URIBuilder("/get_organize_by_id").setParameter("id", this.organizeId)
                .build();
        var response = this.testRestTemplate.getForEntity(url, OrganizeModel.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(StringUtils.isNotBlank(response.getBody().getId()));
        assertEquals(36, response.getBody().getId().length());
        assertEquals("Super Saiyan Son Goku", response.getBody().getName());
        assertEquals(0, response.getBody().getLevel());
        assertNull(response.getBody().getParentOrganize());
        assertEquals(0, response.getBody().getChildOrganizeList().size());
    }

    @BeforeEach
    public void beforeEach() {
        var organizeModel = new OrganizeModel().setName("Super Saiyan Son Goku");
        var organize = this.organizeService.createOrganize(organizeModel);
        this.organizeId = organize.getId();
    }
}
