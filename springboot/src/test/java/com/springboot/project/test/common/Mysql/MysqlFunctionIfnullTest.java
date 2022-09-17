package com.springboot.project.test.common.Mysql;

import static org.junit.jupiter.api.Assertions.assertThrows;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import com.springboot.project.common.mysql.MysqlFunction;
import com.springboot.project.test.BaseTest;

public class MysqlFunctionIfnullTest extends BaseTest {

    @Test
    public void test() {
        assertThrows(RuntimeException.class, () -> {
            MysqlFunction.ifnull(Long.valueOf(1), 0);
        });
        assertThrows(RuntimeException.class, () -> {
            MysqlFunction.ifnull(Integer.valueOf(1), 0);
        });
        assertThrows(RuntimeException.class, () -> {
            MysqlFunction.ifnull(new BigDecimal(1), 0);
        });
    }

}
