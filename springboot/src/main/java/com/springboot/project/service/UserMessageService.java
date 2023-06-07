package com.springboot.project.service;

import java.util.Date;
import java.util.List;
import com.fasterxml.uuid.Generators;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import com.springboot.project.entity.UserMessageEntity;
import com.springboot.project.model.PaginationModel;
import com.springboot.project.model.UserMessageModel;

@Service
public class UserMessageService extends BaseService {

    public UserMessageModel sendMessage(UserMessageModel userMessageModel) {
        var userId = userMessageModel.getUser().getId();
        var userEntity = this.UserEntity().where(s -> s.getId().equals(userId)).getOnlyValue();
        var userMessageEntity = new UserMessageEntity().setId(Generators.timeBasedGenerator().generate().toString())
                .setCreateDate(new Date())
                .setUpdateDate(new Date()).setContent(userMessageModel.getContent()).setIsRecall(false)
                .setUser(userEntity);

        if (StringUtils.isNotBlank(userMessageModel.getUrl())) {
            var storageFileModel = this.storage.storageUrl(userMessageModel.getUrl());
            userMessageEntity.setFolderName(storageFileModel.getFolderName())
                    .setFolderSize(storageFileModel.getFolderSize())
                    .setFileName(storageFileModel.getFileName()).setContent("");
        }
        this.entityManager.persist(userEntity);

        return this.userMessageFormatter.formatForUserId(userMessageEntity, userId);
    }

    public void recallMessage(String id) {
        var userMessageEntity = this.UserMessageEntity().where(s -> s.getId().equals(id)).getOnlyValue();
        userMessageEntity.setIsRecall(true);
        userMessageEntity.setUpdateDate(new Date());
        this.entityManager.merge(userMessageEntity);
    }

    public UserMessageModel getUserMessageById(String id, String userId) {
        var userMessageEntity = this.UserMessageEntity().where(s -> s.getId().equals(id)).getOnlyValue();
        return this.userMessageFormatter.format(userMessageEntity);
    }

    public List<UserMessageModel> getMessageListOnlyContainsOneByPageNum(Long pageNum, String userId) {
        var stream = this.UserMessageEntity().sortedBy(s -> s.getId())
                .sortedBy(s -> s.getCreateDate());
        var userMessageList = new PaginationModel<>(pageNum, 1L, stream,
                (s) -> this.userMessageFormatter.formatForUserId(s, userId)).getList();
        return userMessageList;
    }

    public List<UserMessageModel> getMessageListByLastTwentyMessages(String userId) {
        var userMessageList = this.UserMessageEntity()
                .sortedDescendingBy(s -> s.getId())
                .sortedDescendingBy(s -> s.getCreateDate())
                .limit(20)
                .map(s -> this.userMessageFormatter.formatForUserId(s, userId)).toList();
        return userMessageList;
    }
}
