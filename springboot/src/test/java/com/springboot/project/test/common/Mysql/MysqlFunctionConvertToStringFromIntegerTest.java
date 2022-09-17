package com.springboot.project.test.common.Mysql;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import com.springboot.project.common.mysql.MysqlFunction;
import com.springboot.project.test.BaseTest;

public class MysqlFunctionConvertToStringFromIntegerTest extends BaseTest {

    @Test
    public void test() {
        assertThrows(RuntimeException.class, () -> {
            MysqlFunction.convertToString(15);
        });
    }

}
