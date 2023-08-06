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
        assertEquals("Son Gohan", response.getBody().getName());
        assertEquals(1, response.getBody().getChildOrganizeCount());
    }

    @BeforeEach
    public void beforeEach() {
        {
            var organizeModel = new OrganizeModel().setName("Super Saiyan Son Goku");
            this.organizeId = this.organizeService.createOrganize(organizeModel).getId();
            var childOrganizeModel = new OrganizeModel().setName("Son Gohan")
                    .setParentOrganize(new OrganizeModel().setId(this.organizeId));
            this.childOrganizeId = this.organizeService.createOrganize(childOrganizeModel).getId();
            this.organizeService
                    .createOrganize(new OrganizeModel().setName("Pan")
                            .setParentOrganize(new OrganizeModel().setId(childOrganizeId)));
        }
        {
            var organizeModel = new OrganizeModel().setName("piccolo");
            this.parentOrganizeIdOfMove = this.organizeService.createOrganize(organizeModel).getId();
        }
    }
}
