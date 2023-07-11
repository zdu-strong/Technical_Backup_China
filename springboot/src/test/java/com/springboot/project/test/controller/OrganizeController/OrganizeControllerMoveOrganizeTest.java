package com.springboot.project.test.controller.OrganizeController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.net.URISyntaxException;
import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import com.springboot.project.model.OrganizeModel;
import com.springboot.project.test.BaseTest;

public class OrganizeControllerMoveOrganizeTest extends BaseTest {

    private String organizeId;
    private String childOrganizeId;
    private String parentOrganizeIdOfMove;

    @Test
    public void test() throws URISyntaxException {
        var url = new URIBuilder("/move_organize").setParameter("organizeId", this.childOrganizeId)
                .setParameter("targetParentOrganizeId", this.parentOrganizeIdOfMove)
                .build();
        var response = this.testRestTemplate.postForEntity(url, null, OrganizeModel.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody().getId());
        assertNotEquals(this.childOrganizeId, response.getBody().getId());
        assertNotEquals(this.organizeId, response.getBody().getParentOrganize().getId());
        assertEquals("孙悟饭", response.getBody().getName());
        assertEquals(0, response.getBody().getChildOrganizeList().size());
        assertEquals(this.parentOrganizeIdOfMove, response.getBody().getParentOrganize().getId());
    }

    @BeforeEach
    public void beforeEach() {
        {
            var organizeModel = new OrganizeModel().setName("超级赛亚人孙悟空");
            this.organizeId = this.organizeService.createOrganize(organizeModel).getId();
            var childOrganizeModel = new OrganizeModel().setName("孙悟饭")
                    .setParentOrganize(new OrganizeModel().setId(this.organizeId));
            this.childOrganizeId = this.organizeService.createOrganize(childOrganizeModel).getId();
        }
        {
            var organizeModel = new OrganizeModel().setName("比克");
            this.parentOrganizeIdOfMove = this.organizeService.createOrganize(organizeModel).getId();
        }
    }
}
