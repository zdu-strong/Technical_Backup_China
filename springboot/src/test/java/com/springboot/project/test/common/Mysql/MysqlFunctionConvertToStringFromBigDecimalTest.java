package com.springboot.project.test.common.Mysql;

import static org.junit.jupiter.api.Assertions.assertThrows;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import com.springboot.project.common.mysql.MysqlFunction;
import com.springboot.project.test.BaseTest;

public class MysqlFunctionConvertToStringFromBigDecimalTest extends BaseTest {

    @Test
    public void test() {
        assertThrows(RuntimeException.class, () -> {
            MysqlFunction.convertToString(new BigDecimal("12.59"));
        });
    }

}
