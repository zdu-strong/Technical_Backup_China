package com.springboot.project.test.common.SpringbootProjectApplicationTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import com.springboot.project.SpringbootProjectApplication;
import com.springboot.project.test.BaseTest;

public class SpringbootProjectApplicationTest extends BaseTest {

    @Test
    public void test() {
        assertNotNull(SpringbootProjectApplication.class, "Startup class does not exist");
    }

}
