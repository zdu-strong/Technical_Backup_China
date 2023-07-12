package com.springboot.project.test.controller.OrganizeController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.net.URISyntaxException;
import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import com.springboot.project.model.OrganizeModel;
import com.springboot.project.test.BaseTest;

public class OrganizeControllerDeleteOrganizeTest extends BaseTest {

    private String organizeId;

    @Test
    public void test() throws URISyntaxException {
        var url = new URIBuilder("/delete_organize").setParameter("id", this.organizeId)
                .build();
        var response = this.testRestTemplate.exchange(url, HttpMethod.DELETE, new HttpEntity<>(null), Throwable.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @BeforeEach
    public void beforeEach() {
        var organizeModel = new OrganizeModel().setName("Super Saiyan Son Goku");
        var organize = this.organizeService.createOrganize(organizeModel);
        this.organizeId = organize.getId();
    }
}
