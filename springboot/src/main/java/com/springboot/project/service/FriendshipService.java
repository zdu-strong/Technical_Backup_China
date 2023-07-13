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
                .where(s -> (s.getUser().getId().equals(userId)
                        && s.getFriend().getId().equals(friendId))
                        || (s.getUser().getId().equals(friendId)
                                && s.getFriend().getId().equals(userId)))
                .toList();
        if (friendshipList.size() == 2) {
            return;
        }
        if (friendshipList.size() == 1 && userId.equals(friendId)) {
            return;
        }

        for (var friendship : friendshipList) {
            friendship.setUser(null);
            friendship.setFriend(null);
            this.entityManager.remove(friendship);
        }

        {
            var user = this.UserEntity()
                    .where(s -> s.getId().equals(userId))
                    .where(s -> !s.getIsDeleted())
                    .getOnlyValue();
            var friend = this.UserEntity()
                    .where(s -> s.getId().equals(friendId))
                    .where(s -> !s.getIsDeleted())
                    .getOnlyValue();

            var friendshipEntity = new FriendshipEntity();
            friendshipEntity.setId(Generators.timeBasedGenerator().generate().toString());
            friendshipEntity.setIsFriend(false);
            friendshipEntity.setIsInBlacklist(false);
            friendshipEntity.setCreateDate(new Date());
            friendshipEntity.setUpdateDate(new Date());
            friendshipEntity.setHasInitiative(true);
            friendshipEntity.setSecretKeyOfAES(aesOfUser);
            friendshipEntity.setUser(user);
            friendshipEntity.setFriend(friend);
            this.entityManager.persist(friendshipEntity);
        }

        {
            if (!userId.equals(friendId)) {
                var user = this.UserEntity()
                        .where(s -> s.getId().equals(userId))
                        .where(s -> !s.getIsDeleted())
                        .getOnlyValue();
                var friend = this.UserEntity()
                        .where(s -> s.getId().equals(friendId))
                        .where(s -> !s.getIsDeleted())
                        .getOnlyValue();

                var friendshipEntity = new FriendshipEntity();
                friendshipEntity.setId(Generators.timeBasedGenerator().generate().toString());
                friendshipEntity.setIsFriend(false);
                friendshipEntity.setIsInBlacklist(false);
                friendshipEntity.setCreateDate(new Date());
                friendshipEntity.setUpdateDate(new Date());
                friendshipEntity.setHasInitiative(false);
                friendshipEntity.setSecretKeyOfAES(aesOfFriend);
                friendshipEntity.setUser(friend);
                friendshipEntity.setFriend(user);
                this.entityManager.persist(friendshipEntity);
            }
        }
    }

    public void addToFriendList(String userId, String friendId) {
        var friendshipEntity = this.FriendshipEntity()
                .where(s -> s.getUser().getId().equals(userId))
                .where(s -> s.getFriend().getId().equals(friendId))
                .where(s -> !s.getUser().getIsDeleted())
                .where(s -> !s.getFriend().getIsDeleted())
                .getOnlyValue();
        friendshipEntity.setIsInBlacklist(false);
        friendshipEntity.setIsFriend(true);
        this.entityManager.merge(friendshipEntity);
    }

    public void addToBlacklist(String userId, String friendId) {
        var friendshipEntity = this.FriendshipEntity()
                .where(s -> s.getUser().getId().equals(userId))
                .where(s -> s.getFriend().getId().equals(friendId))
                .where(s -> !s.getUser().getIsDeleted())
                .where(s -> !s.getFriend().getIsDeleted())
                .getOnlyValue();
        friendshipEntity.setIsInBlacklist(true);
        friendshipEntity.setIsFriend(false);
        this.entityManager.merge(friendshipEntity);
    }

    public void deleteFromBlacklist(String userId, String friendId) {
        var friendshipEntity = this.FriendshipEntity()
                .where(s -> s.getUser().getId().equals(userId))
                .where(s -> s.getFriend().getId().equals(friendId))
                .where(s -> !s.getUser().getIsDeleted())
                .where(s -> !s.getFriend().getIsDeleted())
                .getOnlyValue();
        friendshipEntity.setIsInBlacklist(false);
        this.entityManager.merge(friendshipEntity);
    }

    public void deleteFromFriendList(String userId, String friendId) {
        var friendshipEntity = this.FriendshipEntity()
                .where(s -> s.getUser().getId().equals(userId))
                .where(s -> s.getFriend().getId().equals(friendId))
                .where(s -> !s.getUser().getIsDeleted())
                .where(s -> !s.getFriend().getIsDeleted())
                .getOnlyValue();
        friendshipEntity.setIsInBlacklist(false);
        friendshipEntity.setIsFriend(false);
        this.entityManager.merge(friendshipEntity);
    }

    public FriendshipModel getFriendship(String userId, String friendId) {
        var friendshipEntity = this.FriendshipEntity()
                .where(s -> s.getUser().getId().equals(userId))
                .where(s -> s.getFriend().getId().equals(friendId))
                .getOnlyValue();
        return this.friendshipFormatter.format(friendshipEntity);
    }

    public PaginationModel<FriendshipModel> getFriendList(Long pageNum, Long pageSize, String userId) {
        var stream = this.FriendshipEntity()
                .where(s -> s.getUser().getId().equals(userId))
                .where(s -> !s.getUser().getIsDeleted())
                .where(s -> !s.getFriend().getIsDeleted())
                .where(s -> !s.getIsInBlacklist())
                .where(s -> s.getIsFriend());
        return new PaginationModel<>(pageNum, pageSize, stream, (s) -> this.friendshipFormatter.format(s));
    }

    public PaginationModel<FriendshipModel> getStrangerList(Long pageNum, Long pageSize, String userId) {
        var userEntity = this.UserEntity().where(s -> s.getId().equals(userId)).where(s -> !s.getIsDeleted())
                .getOnlyValue();
        var stream = this.UserEntity()
                .where(s -> !s.getIsDeleted())
                .where((s, t) -> !t.stream(FriendshipEntity.class)
                        .where(m -> m.getUser().getId().equals(userId))
                        .where(m -> m.getFriend().getId().equals(s.getId()))
                        .where(m -> m.getIsInBlacklist() || m.getIsFriend())
                        .exists())
                .leftOuterJoin((s, t) -> t.stream(FriendshipEntity.class),
                        (s, t) -> t.getUser().getId().equals(userId)
                                && t.getFriend().getId().equals(s.getId()));
        return new PaginationModel<>(pageNum, pageSize, stream,
                (s) -> this.friendshipFormatter.format(s.getTwo(), userEntity, s.getOne()));
    }

    public PaginationModel<FriendshipModel> getBlackList(Long pageNum, Long pageSize, String userId) {
        var stream = this.FriendshipEntity()
                .where(s -> s.getUser().getId().equals(userId))
                .where(s -> !s.getUser().getIsDeleted())
                .where(s -> !s.getFriend().getIsDeleted())
                .where(s -> s.getIsInBlacklist());
        return new PaginationModel<>(pageNum, pageSize, stream, (s) -> this.friendshipFormatter.format(s));
    }

}
