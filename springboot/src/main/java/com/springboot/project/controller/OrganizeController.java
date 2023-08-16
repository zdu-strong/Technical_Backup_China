package com.springboot.project.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.project.model.OrganizeModel;

@RestController
public class OrganizeController extends BaseController {

    @PostMapping("/create_organize")
    public ResponseEntity<?> createOrganize(@RequestBody OrganizeModel organizeModel) {

        if (organizeModel.getParentOrganize() != null) {
            var parentOrganizeId = organizeModel.getParentOrganize().getId();
            if (StringUtils.isNotBlank(parentOrganizeId)) {
                this.organizeService.checkExistOrganize(parentOrganizeId);
            }
        }

        var organize = this.organizeService.createOrganize(organizeModel);
        return ResponseEntity.ok(organize);
    }

    @DeleteMapping("/delete_organize")
    public ResponseEntity<?> deleteOrganize(@RequestParam String id) {

        this.organizeService.checkExistOrganize(id);

        this.organizeService.deleteOrganize(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/get_organize_by_id")
    public ResponseEntity<?> getOrganizeById(@RequestParam String id) {

        this.organizeService.checkExistOrganize(id);

        var organizeModel = this.organizeService.getOrganize(id);
        return ResponseEntity.ok(organizeModel);
    }

    @PostMapping("/move_organize")
    public ResponseEntity<?> moveOrganize(
            @RequestParam String organizeId,
            @RequestParam String targetParentOrganizeId) {

        this.organizeService.checkExistOrganize(organizeId);
        this.organizeService.checkExistOrganize(targetParentOrganizeId);

        var organizeModel = this.organizeService.moveOrganize(organizeId, targetParentOrganizeId);
        return ResponseEntity.ok(organizeModel);
    }
}
