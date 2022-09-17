package com.springboot.project.test.common.database;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.ZoneOffset;
import java.util.Date;
import java.util.TimeZone;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.springboot.project.common.database.JPQLFunction;
import com.springboot.project.test.BaseTest;

public class JPQLFunctionFormatDateAsYearTest extends BaseTest {
    private String zoneOffset;

    @Test
    public void test() {
        assertThrows(RuntimeException.class, () -> {
            JPQLFunction.formatDateAsYear(new Date(), this.zoneOffset);
        });
    }

    @BeforeEach
    public void beforeEach() {
        this.zoneOffset = ZoneOffset.ofTotalSeconds(TimeZone.getTimeZone("Asia/Shanghai").getRawOffset() / 1000)
                .getId();
    }

}
