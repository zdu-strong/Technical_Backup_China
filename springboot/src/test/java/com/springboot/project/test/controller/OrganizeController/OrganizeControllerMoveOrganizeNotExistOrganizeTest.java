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

public class OrganizeControllerMoveOrganizeNotExistOrganizeTest extends BaseTest {

    private String organizeId;
    private String parentOrganizeIdOfMove;

    @Test
    public void test() throws URISyntaxException {
        var url = new URIBuilder("/move_organize")
                .setParameter("organizeId", this.organizeId)
                .setParameter("targetParentOrganizeId", this.parentOrganizeIdOfMove)
                .build();
        var response = this.testRestTemplate.postForEntity(url, null, Throwable.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Organize does not exist", response.getBody().getMessage());
    }

    @BeforeEach
    public void beforeEach() {
        this.organizeId = Generators.timeBasedGenerator().generate().toString();
        var organizeModel = new OrganizeModel().setName("比克");
        this.parentOrganizeIdOfMove = this.organizeService.createOrganize(organizeModel).getId();
    }
}
