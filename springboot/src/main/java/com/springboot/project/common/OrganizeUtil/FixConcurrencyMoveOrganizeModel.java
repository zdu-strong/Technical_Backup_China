package com.springboot.project.common.OrganizeUtil;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class FixConcurrencyMoveOrganizeModel {

    private Boolean hasNext;
    private String parentOrganizeId;
    private String organizeId;
}
