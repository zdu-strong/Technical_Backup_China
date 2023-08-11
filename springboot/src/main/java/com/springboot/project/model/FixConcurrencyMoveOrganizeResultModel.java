package com.springboot.project.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class FixConcurrencyMoveOrganizeResultModel {

    private Boolean hasNext;
    private String parentOrganizeId;
    private String organizeId;
}
