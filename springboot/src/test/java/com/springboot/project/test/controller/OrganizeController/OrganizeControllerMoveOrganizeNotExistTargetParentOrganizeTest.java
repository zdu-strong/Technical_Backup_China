package com.springboot.project.test.controller.OrganizeController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.net.URISyntaxException;
import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import com.fasterxml.uuid.Generators;
import com.springboot.project.model.OrganizeModel;
import com.springboot.project.test.BaseTest;

public class OrganizeControllerMoveOrganizeNotExistTargetParentOrganizeTest extends BaseTest {

    private String childOrganizeId;
    private String targetParentOrganizeId;

    @Test
    public void test() throws URISyntaxException {
        var url = new URIBuilder("/move_organize")
                .setParameter("organizeId", this.childOrganizeId)
                .setParameter("targetParentOrganizeId", this.targetParentOrganizeId)
                .build();
        var response = this.testRestTemplate.postForEntity(url, null, Throwable.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Organize does not exist", response.getBody().getMessage());
    }

    @BeforeEach
    public void beforeEach() {
        var organizeModel = new OrganizeModel().setName("Super Saiyan Son Goku");
        var organizeId = this.organizeService.createOrganize(organizeModel).getId();
        var childOrganizeModel = new OrganizeModel().setName("Son Gohan")
                .setParentOrganize(new OrganizeModel().setId(organizeId));
        this.childOrganizeId = this.organizeService.createOrganize(childOrganizeModel).getId();
        this.targetParentOrganizeId = Generators.timeBasedGenerator().generate().toString();
    }
}
