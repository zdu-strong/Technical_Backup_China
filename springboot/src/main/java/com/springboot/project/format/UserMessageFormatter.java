package com.springboot.project.format;

import java.nio.file.Paths;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.springboot.project.common.database.JPQLFunction;
import com.springboot.project.entity.UserMessageEntity;
import com.springboot.project.model.UserMessageModel;
import com.springboot.project.model.UserModel;
import com.springboot.project.service.BaseService;

@Service
public class UserMessageFormatter extends BaseService {

    public UserMessageModel format(UserMessageEntity userMessageEntity) {
        var userMessage = new UserMessageModel().setId(userMessageEntity.getId())
                .setContent(userMessageEntity.getContent())
                .setCreateDate(userMessageEntity.getCreateDate()).setUpdateDate(userMessageEntity.getUpdateDate())
                .setIsDelete(false)
                .setIsRecall(userMessageEntity.getIsRecall())
                .setUser(new UserModel().setId(userMessageEntity.getUser().getId()));
        if (!userMessage.getIsRecall() && StringUtils.isNotBlank(userMessageEntity.getFolderName())) {
            userMessage
                    .setUrl(this.storage.getResoureUrlFromResourcePath(
                            Paths.get(userMessageEntity.getFolderName(), userMessageEntity.getFileName()).toString()));
        }
        if (userMessage.getIsRecall() || StringUtils.isNotBlank(userMessageEntity.getFolderName())) {
            userMessage.setContent("");
        }
        return userMessage;
    }

    public UserMessageModel formatForUserId(UserMessageEntity userMessageEntity, String userId) {
        var userMessage = this.format(userMessageEntity);
        userMessage.setTotalPage(this.UserMessageEntity().count());
        var crateDate = userMessage.getCreateDate();
        var id = userMessage.getId();
        var pageNum = this.UserMessageEntity()
                .where(s -> crateDate.after(s.getCreateDate())
                        || (crateDate.equals(s.getCreateDate())
                                && JPQLFunction.isSortAtBefore(s.getId(), id)))
                .count();
        userMessage.setPageNum(Long.valueOf(pageNum).intValue() + 1);
        if (!userMessage.getUser().getId().equals(userId)) {
            userMessage.setIsDelete(false);
        }
        return userMessage;
    }
}
