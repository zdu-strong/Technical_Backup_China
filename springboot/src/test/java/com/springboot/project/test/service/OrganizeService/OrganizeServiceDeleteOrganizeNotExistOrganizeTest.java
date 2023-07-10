package com.springboot.project.test.service.OrganizeService;

import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.fasterxml.uuid.Generators;
import com.springboot.project.test.BaseTest;

public class OrganizeServiceDeleteOrganizeNotExistOrganizeTest extends BaseTest {

    private String organizeId;

    @Test
    public void test() {
        assertThrows(NoSuchElementException.class, () -> {
            this.organizeService.getOrganize(organizeId);
        });
    }

    @BeforeEach
    public void beforeEach() {
        this.organizeId = Generators.timeBasedGenerator().generate().toString();
    }

}
