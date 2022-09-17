package com.springboot.project.common.storage;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import com.alibaba.fastjson.JSON;
import com.fasterxml.uuid.Generators;
import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jinq.orm.stream.JinqStream;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import com.springboot.project.model.StorageFileModel;

@Component
public class BaseStorageSave extends BaseStorageCreateTempFile {
    public StorageFileModel storageResource(Resource resource) {
        try {
            var storageFileModel = new StorageFileModel()
                    .setFolderName(Generators.timeBasedGenerator().generate().toString());

            /* 设置文件名称 */
            storageFileModel.setFileName(this.getFileNameFromResource(resource));

            /* 设置文件夹大小 */
            if (resource.isFile()) {
                File sourceFile = resource.getFile();
                if (!sourceFile.exists()) {
                    throw new RuntimeException("Resource does not exist");
                }
                if (sourceFile.isDirectory()) {
                    storageFileModel.setFolderSize(FileUtils.sizeOfDirectory(sourceFile));
                } else {
                    storageFileModel.setFolderSize(resource.contentLength());
                }
            } else {
                storageFileModel.setFolderSize(resource.contentLength());
            }

            /* 获取相对路径 */
            String relativePath = this.getRelativePathFromResourcePath(storageFileModel.getFolderName());
            if (storageFileModel.getFileName() != null) {
                relativePath = this.getRelativePathFromResourcePath(
                        Paths.get(storageFileModel.getFolderName(), storageFileModel.getFileName()).toString());
            }

            /* 设置相对路径 */
            storageFileModel.setRelativePath(relativePath);

            /* 设置相对url */
            storageFileModel.setRelativeUrl(this.getResoureUrlFromResourcePath(relativePath));

            /* 设置相对下载url */
            storageFileModel.setRelativeDownloadUrl("/download" + storageFileModel.getRelativeUrl());

            if (resource.isFile()) {
                File sourceFile = resource.getFile();
                if (!sourceFile.exists()) {
                    throw new RuntimeException("Resource does not exist");
                }
                if (sourceFile.isDirectory()) {
                    FileUtils.copyDirectory(sourceFile,
                            new File(this.getRootPath(), storageFileModel.getRelativePath()));
                } else {
                    FileUtils.copyFile(sourceFile, new File(this.getRootPath(), storageFileModel.getRelativePath()));
                }
                if (this.cloud.enabled()) {
                    this.cloud.storageResource(sourceFile, storageFileModel.getRelativePath());
                }
            } else {
                try (var input = resource.getInputStream()) {
                    FileUtils.copyToFile(input, new File(this.getRootPath(), storageFileModel.getRelativePath()));
                }
                if (this.cloud.enabled()) {
                    this.cloud.storageResource(new File(this.getRootPath(), storageFileModel.getRelativePath()),
                            storageFileModel.getRelativePath());
                    this.delete(new File(this.getRootPath(), storageFileModel.getRelativePath()));
                }
            }
            return storageFileModel;
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public StorageFileModel storageResource(MultipartFile file) {
        var tempFile = this.createTempFile(file);
        var storageFileModel = new StorageFileModel()
                .setFolderName(tempFile.getParentFile().getName());
        storageFileModel.setFolderSize(file.getSize());
        storageFileModel.setFileName(file.getOriginalFilename());
        storageFileModel.setRelativePath(this.getRelativePathFromResourcePath(
                Paths.get(storageFileModel.getFolderName(), storageFileModel.getFileName()).toString()));
        storageFileModel.setRelativeUrl(this.getResoureUrlFromResourcePath(storageFileModel.getRelativePath()));
        storageFileModel.setRelativeDownloadUrl("/download" + storageFileModel.getRelativePath());
        if (this.cloud.enabled()) {
            this.cloud.storageResource(tempFile, storageFileModel.getRelativePath());
            this.delete(tempFile);
        }
        return storageFileModel;
    }

    public StorageFileModel storageUrl(String url) {
        var mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setRequestURI(url);
        var relativePath = this.getRelativePathFromRequest(mockHttpServletRequest);
        var storageFileModel = new StorageFileModel().setFolderName(
                JinqStream.from(Lists.newArrayList(StringUtils.split(relativePath, "/"))).findFirst().get());
        var resource = this.getResourceFromRequest(mockHttpServletRequest);
        storageFileModel.setFolderSize(this.getResourceSizeByRelativePath(relativePath));
        storageFileModel.setFileName(this.getFileNameFromResource(resource));
        storageFileModel.setRelativePath(relativePath);

        /* 设置相对url */
        storageFileModel.setRelativeUrl(this.getResoureUrlFromResourcePath(relativePath));

        /* 设置相对下载url */
        storageFileModel.setRelativeDownloadUrl("/download" + storageFileModel.getRelativeUrl());
        return storageFileModel;
    }

    private Long getResourceSizeByRelativePath(String relativePathOfResource) {
        try {
            var relativePath = this.getRelativePathFromResourcePath(relativePathOfResource);
            var request = new MockHttpServletRequest();
            request.setRequestURI(this.getResoureUrlFromResourcePath(relativePath));
            var resource = this.getResourceFromRequest(request);
            if (this.cloud.enabled()) {
                if (resource instanceof ByteArrayResource) {
                    try (var input = resource.getInputStream()) {
                        var jsonString = IOUtils.toString(input, StandardCharsets.UTF_8);
                        var nameListOfChildFileAndChildFolder = JSON.parseArray(jsonString, String.class);
                        return JinqStream.from(nameListOfChildFileAndChildFolder)
                                .select(nameOfChildFileAndChildFolder -> this.getResourceSizeByRelativePath(
                                        Paths.get(relativePath, nameOfChildFileAndChildFolder).toString()))
                                .sumLong(s -> s);
                    }
                } else {
                    return resource.contentLength();
                }
            } else {
                var file = new File(this.getRootPath(), relativePath);
                if (file.isDirectory()) {
                    return FileUtils.sizeOfDirectory(new File(this.getRootPath(), relativePath));
                } else {
                    return file.length();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public StorageFileModel storageUrlAsFolderAfterUnzip(String url) {
        var mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setRequestURI(url);
        var tempFolder = this
                .createTempFolderByDecompressingZipResource(this.getResourceFromRequest(mockHttpServletRequest));
        var relativePath = tempFolder.getName();
        var storageFileModel = new StorageFileModel().setFolderName(tempFolder.getName());
        storageFileModel.setFolderSize(FileUtils.sizeOfDirectory(tempFolder));
        storageFileModel.setFileName(null);
        storageFileModel.setRelativePath(relativePath);

        /* 设置相对url */
        storageFileModel.setRelativeUrl(this.getResoureUrlFromResourcePath(relativePath));

        /* 设置相对下载url */
        storageFileModel.setRelativeDownloadUrl("/download" + storageFileModel.getRelativeUrl());
        if (this.cloud.enabled()) {
            this.cloud.storageResource(tempFolder, relativePath);
            this.delete(tempFolder);
        }
        return storageFileModel;
    }

}
