package com.springboot.project.test.service.OrganizeService;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import com.fasterxml.uuid.Generators;
import com.springboot.project.test.BaseTest;

public class OrganizeServiceCheckExistOrganizeNotExistOrganizeTest extends BaseTest {

    @Test
    public void test() {
        assertThrows(ResponseStatusException.class, () -> {
            this.organizeService.checkExistOrganize(Generators.timeBasedGenerator().generate().toString());
        });
    }

}
