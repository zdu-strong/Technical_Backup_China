package com.springboot.project.common.OrganizeUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.springboot.project.service.BaseService;
import com.springboot.project.service.OrganizeClosureService;
import com.springboot.project.service.OrganizeShadowService;

@Service
public class BaseOrganizeUtil extends BaseService {

    @Autowired
    protected OrganizeShadowService organizeShadowService;

    @Autowired
    protected OrganizeClosureService organizeClosureService;

}
