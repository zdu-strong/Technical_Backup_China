package com.springboot.project.test.properties.AuthorizationEmailProperties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import com.springboot.project.test.BaseTest;

public class AuthorizationEmailPropertiesTest extends BaseTest {

    @Test
    public void test() {
        assertEquals("zdu_202104121637@163.com",
                this.authorizationEmailProperties.getSenderEmail());
    }
}
