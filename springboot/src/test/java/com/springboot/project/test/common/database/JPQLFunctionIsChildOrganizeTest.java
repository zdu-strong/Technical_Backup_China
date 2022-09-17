package com.springboot.project.test.common.database;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.fasterxml.uuid.Generators;
import com.springboot.project.common.database.JPQLFunction;
import com.springboot.project.test.BaseTest;

public class JPQLFunctionIsChildOrganizeTest extends BaseTest {
    private String childOrganizeId;
    private String parentOrganizeId;

    @Test
    public void test() {
        assertThrows(RuntimeException.class, () -> {
            JPQLFunction.isChildOrganize(this.childOrganizeId, this.parentOrganizeId);
        });
    }

    @BeforeEach
    public void beforeEach() {
        this.childOrganizeId = Generators.timeBasedGenerator().generate().toString();
        this.parentOrganizeId = Generators.timeBasedGenerator().generate().toString();
    }

}
