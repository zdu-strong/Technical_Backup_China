package com.springboot.project.test.controller.AuthorizationController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URISyntaxException;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import com.springboot.project.model.UserModel;
import com.springboot.project.test.BaseTest;

public class AuthorizationControllerGetNewAccountTest extends BaseTest {

    @Test
    public void test() throws URISyntaxException {
        var url = new URIBuilder("/sign_up/get_new_account").build();
        var response = this.testRestTemplate.postForEntity(url, null, UserModel.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(StringUtils.isNotBlank(response.getBody().getId()));
        assertTrue(StringUtils.isNotBlank(response.getBody().getPublicKeyOfRSA()));
        assertTrue(StringUtils.isBlank(response.getBody().getUsername()));
        assertTrue(StringUtils.isBlank(response.getBody().getEmail()));
        assertTrue(StringUtils.isBlank(response.getBody().getPassword()));
        assertTrue(StringUtils.isBlank(response.getBody().getPrivateKeyOfRSA()));
        assertFalse(response.getBody().getHasRegistered());
        assertNull(response.getBody().getUserEmailList());
    }
}
