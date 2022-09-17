package com.springboot.project.test.common.Mysql;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.fasterxml.uuid.Generators;
import com.springboot.project.common.mysql.MysqlFunction;
import com.springboot.project.test.BaseTest;

public class MysqlFunctionIsNotDeleteOfOrganizeAndAncestorsTest extends BaseTest {
    private String organizeId;

    @Test
    public void test() {
        assertThrows(RuntimeException.class, () -> {
            MysqlFunction.isNotDeleteOfOrganizeAndAncestors(this.organizeId);
        });
    }

    @BeforeEach
    public void beforeEach() {
        this.organizeId = Generators.timeBasedGenerator().generate().toString();
    }

}
