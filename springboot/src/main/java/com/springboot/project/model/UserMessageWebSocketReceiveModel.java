package com.springboot.project.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UserMessageWebSocketReceiveModel {
    private Integer pageNum;

    private Boolean isCancel;
}
