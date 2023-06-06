package com.springboot.project.common.storage;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.regex.Pattern;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.jinq.orm.stream.JinqStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.springboot.project.common.CloudStorage.CloudStorageImplement;
import com.springboot.project.model.ResourceAccessLegalModel;
import com.springboot.project.properties.StorageRootPathProperties;
import com.springboot.project.service.EncryptDecryptService;
import io.reactivex.rxjava3.core.Observable;

@Component
public class BaseStorage {

    @Autowired
    protected EncryptDecryptService encryptDecryptService;

    @Autowired
    protected StorageRootPathProperties storageRootPathProperties;

    @Autowired
    protected CloudStorageImplement cloud;

    protected String storageRootPath;

    public Observable<String> listRoots() {
        if (this.cloud.enabled()) {
            return Observable.concat(Observable.fromArray(new File(this.getRootPath()).list()),
                    this.cloud.getRootList());
        } else {
            return Observable.fromArray(new File(this.getRootPath()).list());
        }
    }

    public String getRootPath() {
        if (StringUtils.isBlank(storageRootPath)) {
            synchronized (getClass()) {
                if (StringUtils.isBlank(storageRootPath)) {
                    File currentFolderPath = Paths.get(new File("./").getAbsolutePath()).normalize().toFile();
                    String rootPath = "";
                    if (this.storageRootPathProperties.getStorageRootPath().equals("default")) {
                        if (new File(currentFolderPath, ".mvn").isDirectory()) {
                            rootPath = Paths.get(currentFolderPath.getAbsolutePath(), ".mvn/storage").toString();
                        } else {
                            rootPath = Paths.get(currentFolderPath.getAbsolutePath(), "storage").toString();
                        }
                    } else if (this.storageRootPathProperties.getStorageRootPath()
                            .equals("defaultTest-a56b075f-102e-edf3-8599-ffc526ec948a")) {
                        if (new File(currentFolderPath, ".mvn").isDirectory()) {
                            rootPath = Paths.get(currentFolderPath.getAbsolutePath(), "target/storage").toString();
                        } else {
                            rootPath = Paths.get(currentFolderPath.getAbsolutePath(), "storage").toString();
                        }
                    } else {
                        if (StringUtils.isBlank(this.storageRootPathProperties.getStorageRootPath().trim())) {
                            throw new RuntimeException("Unsupported storage root path");
                        }
                        if (new File(this.storageRootPathProperties.getStorageRootPath()).isAbsolute()) {
                            rootPath = this.storageRootPathProperties.getStorageRootPath();
                        } else {
                            rootPath = Paths.get(currentFolderPath.getAbsolutePath(), this.storageRootPathProperties
                                    .getStorageRootPath().replaceAll(Pattern.quote("\\"), "/")).toString();
                        }
                    }
                    rootPath = rootPath.replaceAll("\\\\", "/");
                    new File(rootPath).mkdirs();
                    this.storageRootPath = rootPath;
                }
            }
        }
        return this.storageRootPath;
    }

    protected String getRelativePathFromResourcePath(String relativePathOfResource) {
        String path = "";
        if (Paths.get(relativePathOfResource.replaceAll(Pattern.quote("\\"), "/")).isAbsolute()) {
            throw new RuntimeException("Only relative path can be passed in");
        } else {
            path = relativePathOfResource;
        }

        path = Paths.get(this.getRootPath(), path.replaceAll(Pattern.quote("\\"), "/")).toString();
        path = Paths.get(path).normalize().toString().replaceAll(Pattern.quote("\\"), "/");
        if (!path.startsWith(this.getRootPath())) {
            throw new RuntimeException("Unsupported path");
        }
        if (path.equals(this.getRootPath())) {
            throw new RuntimeException("Unsupported path");
        }
        return Paths.get(this.getRootPath()).relativize(Paths.get(path)).normalize().toString()
                .replaceAll(Pattern.quote("\\"), "/");
    }

    protected String getRelativePathFromRequest(HttpServletRequest request) {
        try {
            var pathSegmentList = new URIBuilder(request.getRequestURI()).getPathSegments().stream()
                    .filter(s -> StringUtils.isNotBlank(s)).toList();
            if (JinqStream.from(pathSegmentList).findFirst().get().equals("resource")) {
                pathSegmentList = JinqStream.from(pathSegmentList).skip(1).toList();
            } else if (JinqStream.from(pathSegmentList).findFirst().get().equals("download")
                    && JinqStream.from(pathSegmentList).skip(1).findFirst().get().equals("resource")) {
                pathSegmentList = JinqStream.from(pathSegmentList).skip(2).toList();
            } else if (JinqStream.from(pathSegmentList).findFirst().get().equals("is_directory")
                    && JinqStream.from(pathSegmentList).skip(1).findFirst().get().equals("resource")) {
                pathSegmentList = JinqStream.from(pathSegmentList).skip(2).toList();
            } else {
                throw new RuntimeException("Unsupported resource path");
            }
            ResourceAccessLegalModel resourceAccessLegalModel = new ObjectMapper().readValue(
                    this.encryptDecryptService.getAES().decryptStr(
                            Base64.getUrlDecoder().decode(JinqStream.from(pathSegmentList).findFirst().get())),
                    ResourceAccessLegalModel.class);
            return this.getRelativePathFromResourcePath(
                    String.join("/", Lists.asList(resourceAccessLegalModel.getRootFolderName(),
                            JinqStream.from(pathSegmentList).skip(1).toArray(String[]::new))));
        } catch (URISyntaxException | JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public String getResoureUrlFromResourcePath(String relativePathOfResource) {
        try {
            String relativePath = this.getRelativePathFromResourcePath(relativePathOfResource);
            String rootFolderName = JinqStream.from(Lists.newArrayList(StringUtils.split(relativePath, "/")))
                    .findFirst().get();
            ResourceAccessLegalModel resourceAccessLegalModel = new ResourceAccessLegalModel();
            resourceAccessLegalModel.setRootFolderName(rootFolderName);
            var pathSegmentList = new ArrayList<String>();
            pathSegmentList.add("resource");
            pathSegmentList
                    .add(Base64.getUrlEncoder().encodeToString(this.encryptDecryptService.getAES().encrypt(
                            new ObjectMapper().writeValueAsString(resourceAccessLegalModel))));
            var pathList = JinqStream.from(Lists.newArrayList(StringUtils.split(relativePath, "/"))).toList();
            if (pathList.size() > 1) {
                pathSegmentList
                        .addAll(JinqStream.from(pathList).skip(1).toList());
            }
            var url = new URIBuilder().setPathSegments(pathSegmentList).build();
            return url.toString();
        } catch (URISyntaxException | JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public String getFileNameFromResource(Resource resource) {
        try {
            if (resource instanceof UrlResource) {
                var pathSegments = Lists
                        .newArrayList(new URIBuilder(((UrlResource) resource).getURI()).getPathSegments());
                Collections.reverse(pathSegments);
                String fileName = pathSegments.stream().findFirst().get();
                return this.getRelativePathFromResourcePath(fileName);
            }
            return this.getRelativePathFromResourcePath(resource.getFilename());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
