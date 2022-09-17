package com.springboot.project.common.storage;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.ArrayUtils;
import org.jinq.orm.stream.JinqStream;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;

@Component
public class BaseStorageGetResourceForRequest extends BaseStorageDeleteResource {
    /**
     * If it is a directory, return like: JSON.toString(["childFile",
     * "childDirectory/"])
     * If it is a directory, return ByteArrayStream, else not.
     * 
     * @param request
     * @return
     */
    public Resource getResourceFromRequest(HttpServletRequest request) {
        String relativePath = this.getRelativePathFromRequest(request);
        if (this.cloud.enabled()) {
            return this.cloud.getResource(relativePath);
        }
        var file = new File(this.getRootPath(), relativePath);
        if (file.isDirectory()) {
            var jsonString = JSON.toJSONString(getChildFileNameListFromDirectory(file));
            return new ByteArrayResource(jsonString.getBytes(StandardCharsets.UTF_8));
        }
        return new FileSystemResource(file);
    }

    private List<String> getChildFileNameListFromDirectory(File file) {
        return Lists.newArrayList(file.listFiles()).stream()
                .map((childFile) -> childFile.getName() + (childFile.isDirectory() ? "/" : ""))
                .toList();
    }

    /**
     * If it is a directory, return like: JSON.toString(["childFile",
     * "childDirectory/"])
     * If it is a directory, return ByteArrayStream, else not.
     * 
     * @return
     */
    public Resource getResourceFromRequest(HttpServletRequest request, long start, long length) {
        String relativePath = this.getRelativePathFromRequest(request);
        if (this.cloud.enabled()) {
            return this.cloud.getResource(relativePath, start, length);
        }
        var file = new File(this.getRootPath(), relativePath);
        if (file.isDirectory()) {
            var jsonString = JSON.toJSONString(getChildFileNameListFromDirectory(file));
            return new ByteArrayResource(ArrayUtils.toPrimitive(
                    Lists.newArrayList(JinqStream.of(Lists.newArrayList(jsonString.getBytes(StandardCharsets.UTF_8)))
                            .skip(start).limit(length).toArray()).toArray(new Byte[] {})));
        }
        return new RangeFileSystemResource(file, start, length);
    }
}
