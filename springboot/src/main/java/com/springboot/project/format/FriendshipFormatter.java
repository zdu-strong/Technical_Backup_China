package com.springboot.project.format;

import org.springframework.stereotype.Service;
import com.springboot.project.entity.*;
import com.springboot.project.model.FriendshipModel;
import com.springboot.project.model.UserModel;
import com.springboot.project.service.BaseService;

@Service
public class FriendshipFormatter extends BaseService {

    public FriendshipModel format(FriendshipEntity friendshipEntity) {
        var friendshipModel = new FriendshipModel().setId(friendshipEntity.getId())
                .setIsBlacklist(friendshipEntity.getIsBlacklist())
                .setIsFriend(!friendshipEntity.getIsBlacklist() && friendshipEntity.getIsFriend())
                .setCreateDate(friendshipEntity.getCreateDate())
                .setUpdateDate(friendshipEntity.getUpdateDate())
                .setUser(new UserModel().setId(friendshipEntity.getUser().getId()))
                .setFriend(this.userFormatter.format(friendshipEntity.getFriend()));
        return friendshipModel;
    }

    public FriendshipModel format(FriendshipEntity friendshipEntity, UserEntity userEntity, UserEntity friendEntity) {
        if (friendshipEntity != null) {
            return this.format(friendshipEntity);
        }
        var friendshipModel = new FriendshipModel().setIsBlacklist(false)
                .setIsFriend(false).setUser(new UserModel().setId(userEntity.getId()))
                .setFriend(this.userFormatter.format(friendEntity));
        return friendshipModel;
    }
}
