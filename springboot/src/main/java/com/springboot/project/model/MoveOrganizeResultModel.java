package com.springboot.project.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class MoveOrganizeResultModel {
    private Boolean hasNext;
    private String organizeId;
    private String targetOrganizeId;
    private String targetParentOrganizeId;
}
