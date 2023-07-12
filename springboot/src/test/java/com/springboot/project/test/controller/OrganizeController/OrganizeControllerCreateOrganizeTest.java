package com.springboot.project.test.controller.OrganizeController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.net.URISyntaxException;
import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import com.springboot.project.model.OrganizeModel;
import com.springboot.project.test.BaseTest;

public class OrganizeControllerCreateOrganizeTest extends BaseTest {

    private OrganizeModel organizeModel;

    @Test
    public void test() throws URISyntaxException {
        var url = new URIBuilder("/create_organize").build();
        var response = this.testRestTemplate.postForEntity(url, new HttpEntity<>(organizeModel), OrganizeModel.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody().getId());
        assertEquals(36, response.getBody().getId().length());
        assertEquals(this.organizeModel.getParentOrganize().getId(), response.getBody().getParentOrganize().getId());
        assertEquals("Son Gohan", response.getBody().getName());
        assertEquals(0, response.getBody().getChildOrganizeList().size());
    }

    @BeforeEach
    public void beforeEach() {
        var parentOrganizeModel = new OrganizeModel().setName("Super Saiyan Son Goku");
        var parentOrganize = this.organizeService.createOrganize(parentOrganizeModel);
        var childOrganizeModel = new OrganizeModel().setName("Son Gohan")
                .setParentOrganize(new OrganizeModel().setId(parentOrganize.getId()));
        this.organizeModel = childOrganizeModel;
    }
}
