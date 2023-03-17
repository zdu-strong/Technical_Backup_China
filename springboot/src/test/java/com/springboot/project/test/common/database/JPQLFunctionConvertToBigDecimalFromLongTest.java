package com.springboot.project.test.common.database;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import com.springboot.project.common.database.JPQLFunction;
import com.springboot.project.test.BaseTest;

public class JPQLFunctionConvertToBigDecimalFromLongTest extends BaseTest {

    @Test
    public void test() {
        assertThrows(RuntimeException.class, () -> {
            JPQLFunction.convertToBigDecimal(12L);
        });
    }

}
