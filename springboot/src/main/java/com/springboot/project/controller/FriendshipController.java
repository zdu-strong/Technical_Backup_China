package com.springboot.project.controller;

import java.io.IOException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FriendshipController extends BaseController {

    @PostMapping("/get_friend_list")
    public ResponseEntity<?> getFriendList() throws IOException {
        this.permissionUtil.checkIsSignIn(request);

        var userId = this.permissionUtil.getUserId(request);

        var pagination = this.friendshipService.getFriendList(1, 100, userId);
        return ResponseEntity.ok(pagination);
    }

}
