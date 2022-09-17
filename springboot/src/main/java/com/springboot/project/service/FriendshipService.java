package com.springboot.project.service;

import java.util.Date;
import org.springframework.stereotype.Service;
import com.fasterxml.uuid.Generators;
import com.springboot.project.entity.FriendshipEntity;
import com.springboot.project.model.FriendshipModel;
import com.springboot.project.model.PaginationModel;

@Service
public class FriendshipService extends BaseService {
    public void createFriendship(String userId, String friendId, String aesOfUser, String aesOfFriend) {
        var friendshipList = this.FriendshipEntity()
                .where(s -> (s.getUser().getId().equals(userId) && s.getFriend().getId().equals(friendId))
                        || (s.getUser().getId().equals(friendId) && s.getFriend().getId().equals(userId)))
                .toList();
        if (friendshipList.size() == 2) {
            return;
        }
        for (var friendship : friendshipList) {
            this.entityManager.remove(friendship);
        }

        {
            var user = this.UserEntity().where(s -> s.getId().equals(userId)).where(s -> s.getDeleteKey().equals(""))
                    .getOnlyValue();
            var friend = this.UserEntity().where(s -> s.getId().equals(friendId))
                    .where(s -> s.getDeleteKey().equals("")).getOnlyValue();

            var friendshipEntity = new FriendshipEntity();
            friendshipEntity.setId(Generators.timeBasedGenerator().generate().toString());
            friendshipEntity.setIsFriend(false);
            friendshipEntity.setIsBlacklist(false);
            friendshipEntity.setCreateDate(new Date());
            friendshipEntity.setUpdateDate(new Date());
            friendshipEntity.setSecretKeyOfAES(aesOfUser);
            friendshipEntity.setUser(user);
            friendshipEntity.setFriend(friend);
            this.entityManager.persist(friendshipEntity);
        }

        {
            var user = this.UserEntity().where(s -> s.getId().equals(userId)).where(s -> s.getDeleteKey().equals(""))
                    .getOnlyValue();
            var friend = this.UserEntity().where(s -> s.getId().equals(friendId))
                    .where(s -> s.getDeleteKey().equals("")).getOnlyValue();

            var friendshipEntity = new FriendshipEntity();
            friendshipEntity.setId(Generators.timeBasedGenerator().generate().toString());
            friendshipEntity.setIsFriend(false);
            friendshipEntity.setIsBlacklist(false);
            friendshipEntity.setCreateDate(new Date());
            friendshipEntity.setUpdateDate(new Date());
            friendshipEntity.setSecretKeyOfAES(aesOfFriend);
            friendshipEntity.setUser(friend);
            friendshipEntity.setFriend(user);
            this.entityManager.persist(friendshipEntity);
        }
    }

    public void addFriend(String userId, String friendId) {
        var friendshipEntity = this.FriendshipEntity().where(s -> s.getUser().getId().equals(userId))
                .where(s -> s.getFriend().getId().equals(friendId))
                .where(s -> s.getUser().getDeleteKey().equals(""))
                .where(s -> s.getFriend().getDeleteKey().equals(""))
                .getOnlyValue();
        friendshipEntity.setIsBlacklist(false);
        friendshipEntity.setIsFriend(true);
        this.entityManager.merge(friendshipEntity);
    }

    public void addBlacklist(String userId, String friendId) {
        var friendshipEntity = this.FriendshipEntity().where(s -> s.getUser().getId().equals(userId))
                .where(s -> s.getFriend().getId().equals(friendId))
                .where(s -> s.getUser().getDeleteKey().equals(""))
                .where(s -> s.getFriend().getDeleteKey().equals(""))
                .getOnlyValue();
        friendshipEntity.setIsBlacklist(true);
        friendshipEntity.setIsFriend(false);
        this.entityManager.merge(friendshipEntity);
    }

    public PaginationModel<FriendshipModel> getFriendList(int pageNum, int pageSize, String userId) {
        var stream = this.FriendshipEntity()
                .where(s -> s.getUser().getId().equals(userId))
                .where(s -> s.getUser().getDeleteKey().equals(""))
                .where(s -> s.getFriend().getDeleteKey().equals(""))
                .where(s -> !s.getIsBlacklist()).where(s -> s.getIsFriend());
        return new PaginationModel<>(pageNum, pageSize, stream, (s) -> this.friendshipFormatter.format(s));
    }

    public PaginationModel<FriendshipModel> getStrangerList(int pageNum, int pageSize, String userId) {
        var userEntity = this.UserEntity().where(s -> s.getId().equals(userId)).where(s -> s.getDeleteKey().equals(""))
                .getOnlyValue();
        var stream = this.UserEntity()
                .where(s -> s.getDeleteKey().equals(""))
                .where((s, t) -> t.stream(FriendshipEntity.class).where(m -> m.getUser().getId().equals(userId))
                        .where(m -> m.getFriend().getId().equals(s.getId()))
                        .where(m -> m.getIsBlacklist() || m.getIsFriend())
                        .count() == 0)
                .leftOuterJoin((s, t) -> t.stream(FriendshipEntity.class),
                        (s, t) -> t.getUser().getId().equals(userId) && t.getFriend().getId().equals(s.getId()));
        return new PaginationModel<>(pageNum, pageSize, stream,
                (s) -> this.friendshipFormatter.format(s.getTwo(), userEntity, s.getOne()));
    }
}
