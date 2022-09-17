package com.springboot.project.controller;

import java.io.IOException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController extends BaseController {

    @GetMapping("/get_user_by_id")
    public ResponseEntity<?> sendMessage(@RequestParam String userId) throws IOException {
        this.permissionUtil.checkIsSignIn(request);

        var userModel = this.userService.getUserById(userId);
        return ResponseEntity.ok(userModel);
    }

}
