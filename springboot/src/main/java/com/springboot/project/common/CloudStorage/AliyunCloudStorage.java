package com.springboot.project.common.CloudStorage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.springboot.project.common.storage.SequenceResource;
import com.springboot.project.properties.AliyunCloudStorageProperties;
import io.reactivex.rxjava3.core.Observable;

@Component
public class AliyunCloudStorage implements CloudStorageInterface {

    @Autowired
    private AliyunCloudStorageProperties aliyunCloudStorageProperties;

    @Override
    public boolean enabled() {
        return this.aliyunCloudStorageProperties.getEnabled();
    }

    @Override
    public void storageResource(File sourceFileOrSourceFolder, String key) {
        var ossClient = this.getOssClientClient();
        try {
            if (sourceFileOrSourceFolder.isDirectory()) {
                key += "/";
                ossClient.putObject(this.aliyunCloudStorageProperties.getBucketName(), key,
                        new ByteArrayInputStream(new byte[] {}));
                for (var childOfSourceFileOrSourceFolder : sourceFileOrSourceFolder.listFiles()) {
                    this.storageResource(childOfSourceFileOrSourceFolder,
                            key + childOfSourceFileOrSourceFolder.getName());
                }
            } else {
                try (var input = new FileSystemResource(sourceFileOrSourceFolder).getInputStream()) {
                    ossClient.putObject(this.aliyunCloudStorageProperties.getBucketName(), key,
                            input);
                } catch (IOException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        } finally {
            ossClient.shutdown();
        }
    }

    @Override
    public void storageResource(SequenceResource sourceFile, String key) {
        var ossClient = this.getOssClientClient();
        try {
            try (var input = sourceFile.getInputStream()) {
                ossClient.putObject(this.aliyunCloudStorageProperties.getBucketName(), key, input);
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        } finally {
            ossClient.shutdown();
        }
    }

    @Override
    public void delete(String key) {
        var ossClient = this.getOssClientClient();
        try {
            ossClient.deleteObject(this.aliyunCloudStorageProperties.getBucketName(), key);
        } finally {
            ossClient.shutdown();
        }
    }

    @Override
    public Resource getResource(String key) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getResource'");
    }

    @Override
    public Resource getResource(String key, long startIndex, long rangeContentLength) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getResource'");
    }

    @Override
    public Observable<String> getRootList() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getRootList'");
    }

    private OSS getOssClientClient() {
        var ossClient = new OSSClientBuilder().build(this.aliyunCloudStorageProperties.getEndpoint(),
                this.aliyunCloudStorageProperties.getAccessKeyId(),
                this.aliyunCloudStorageProperties.getAccessKeySecret());
        return ossClient;
    }

}
