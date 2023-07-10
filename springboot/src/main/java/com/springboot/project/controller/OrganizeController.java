package com.springboot.project.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrganizeController extends BaseController {

    @PostMapping("/organize/move")
    public ResponseEntity<?> moveOrganize(
            @RequestParam String organizeId,
            @RequestParam String targetParentOrganizeId) {

        this.organizeService.checkExistOrganize(organizeId);
        this.organizeService.checkExistOrganize(targetParentOrganizeId);

        var organizeModel = this.organizeService.moveOrganize(organizeId, targetParentOrganizeId);
        return ResponseEntity.ok(organizeModel);
    }
}
