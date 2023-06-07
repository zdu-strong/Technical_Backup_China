package com.springboot.project.common.storage;

import java.nio.charset.StandardCharsets;
import org.apache.commons.lang3.ArrayUtils;
import org.jinq.orm.stream.JinqStream;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.springboot.project.common.ClassPathStorage.ClassPathStorageEnum;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class BaseStorageGetClassPathResourceFromRequest extends BaseStorageDeleteResource {

    protected Boolean isClassPathResource(HttpServletRequest request) {
        var relativePath = this.getRelativePathFromRequest(request);
        var stream = JinqStream.from(Lists.newArrayList(ClassPathStorageEnum.values()));
        return stream.anyMatch(
                s -> s.getRelativePath().equals(relativePath) || s.getRelativePath().contains(relativePath + "/"));
    }

    protected Resource getClassPathResource(HttpServletRequest request) {
        String relativePath = this.getRelativePathFromRequest(request);
        var list = Lists.newArrayList(ClassPathStorageEnum.values());
        if (JinqStream.from(list).anyMatch(s -> s.getRelativePath().equals(relativePath))) {
            return JinqStream.from(list).where(s -> s.getRelativePath().equals(relativePath))
                    .select(s -> s.getResource()).getOnlyValue();
        }
        var jsonString = new Gson().toJson(JinqStream.from(list).where(s -> s.getRelativePath().contains(relativePath +
                "/"))
                .select(s -> JinqStream.from(Lists.newArrayList(s.getRelativePath().split("/")))
                        .skip(relativePath.split("/").length).findFirst()
                        .get())
                .distinct().toList());
        var jsonBytes = jsonString.getBytes(StandardCharsets.UTF_8);
        return new ByteArrayResource(jsonBytes);
    }

    protected Resource getClassPathResource(HttpServletRequest request, long startIndex, long rangeContentLength) {
        String relativePath = this.getRelativePathFromRequest(request);
        var list = Lists.newArrayList(ClassPathStorageEnum.values());
        if (JinqStream.from(list).anyMatch(s -> s.getRelativePath().equals(relativePath))) {
            return JinqStream.from(list).where(s -> s.getRelativePath().equals(relativePath))
                    .select(s -> s.getResource(startIndex, rangeContentLength)).getOnlyValue();
        }

        var jsonString = new Gson().toJson(JinqStream.from(list).where(s -> s.getRelativePath().contains(relativePath +
                "/"))
                .select(s -> JinqStream.from(Lists.newArrayList(s.getRelativePath().split("/")))
                        .skip(relativePath.split("/").length).findFirst()
                        .get())
                .distinct().toList());
        var jsonBytes = jsonString.getBytes(StandardCharsets.UTF_8);
        var bytes = ArrayUtils.toPrimitive(
                JinqStream.from(Lists.newArrayList(ArrayUtils.toObject(jsonBytes))).skip(startIndex)
                        .limit(rangeContentLength)
                        .toList().toArray(new Byte[] {}));
        return new ByteArrayResource(bytes);
    }

}
