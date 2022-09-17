package com.springboot.project.test.common.database;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.fasterxml.uuid.Generators;
import com.springboot.project.common.database.JPQLFunction;
import com.springboot.project.test.BaseTest;

public class JPQLFunctionIsNotDeleteOfOrganizeAndAncestorsTest extends BaseTest {
    private String organizeId;

    @Test
    public void test() {
        assertThrows(RuntimeException.class, () -> {
            JPQLFunction.isNotDeleteOfOrganizeAndAncestors(this.organizeId);
        });
    }

    @BeforeEach
    public void beforeEach() {
        this.organizeId = Generators.timeBasedGenerator().generate().toString();
    }

}
