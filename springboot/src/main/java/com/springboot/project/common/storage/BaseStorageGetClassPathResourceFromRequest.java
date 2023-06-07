package com.springboot.project.common.storage;

import org.jinq.orm.stream.JinqStream;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import com.google.common.collect.Lists;
import com.springboot.project.common.ClassPathStorage.ClassPathStorageEnum;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class BaseStorageGetClassPathResourceFromRequest extends BaseStorageDeleteResource {

    protected Boolean isClassPathResource(HttpServletRequest request) {
        var relativePath = this.getRelativePathFromRequest(request);
        var stream = JinqStream.from(Lists.newArrayList(ClassPathStorageEnum.values()));
        return stream.anyMatch(s -> s.getRelativePath().equals(relativePath));
    }

    protected Resource getClassPathResource(HttpServletRequest request) {
        String relativePath = this.getRelativePathFromRequest(request);
        var stream = JinqStream.from(Lists.newArrayList(ClassPathStorageEnum.values()));
        return stream.where(s -> s.getRelativePath().equals(relativePath)).select(s -> s.getResource()).getOnlyValue();
    }

    protected Resource getClassPathResource(HttpServletRequest request, long startIndex, long rangeContentLength) {
        String relativePath = this.getRelativePathFromRequest(request);
        var stream = JinqStream.from(Lists.newArrayList(ClassPathStorageEnum.values()));
        return stream.where(s -> s.getRelativePath().equals(relativePath))
                .select(s -> s.getResource(startIndex, rangeContentLength)).getOnlyValue();
    }

}
