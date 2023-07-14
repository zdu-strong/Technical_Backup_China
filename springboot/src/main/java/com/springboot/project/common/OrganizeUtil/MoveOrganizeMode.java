package com.springboot.project.common.OrganizeUtil;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class MoveOrganizeMode {
    private Boolean hasNext;
    private String organizeId;
    private String targetOrganizeId;
    private String targetParentOrganizeId;
}
