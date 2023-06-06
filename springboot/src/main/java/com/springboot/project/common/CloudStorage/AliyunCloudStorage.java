package com.springboot.project.common.CloudStorage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ListObjectsV2Request;
import com.aliyun.oss.model.ListObjectsV2Result;
import com.aliyun.oss.model.OSSObjectSummary;
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
        var ossClient = this.getOssClientClient();
        try {
            String nextContinuationToken = null;
            ListObjectsV2Result result = null;

            do {
                ListObjectsV2Request listObjectsV2Request = new ListObjectsV2Request(
                        this.aliyunCloudStorageProperties.getBucketName()).withMaxKeys(200);
                listObjectsV2Request.setPrefix("");
                listObjectsV2Request.setDelimiter("/");
                listObjectsV2Request.setContinuationToken(nextContinuationToken);
                result = ossClient.listObjectsV2(listObjectsV2Request);

                for (OSSObjectSummary objectSummary : result.getObjectSummaries()) {
                    System.out.println(objectSummary.getKey());
                }

                for (String commonPrefix : result.getCommonPrefixes()) {
                    System.out.println(commonPrefix);
                }

                nextContinuationToken = result.getNextContinuationToken();

            } while (result.isTruncated());
        } finally {
            ossClient.shutdown();
        }

        return Observable.fromArray(new String[] {});
    }

    private OSS getOssClientClient() {
        var ossClient = new OSSClientBuilder().build(this.aliyunCloudStorageProperties.getEndpoint(),
                this.aliyunCloudStorageProperties.getAccessKeyId(),
                this.aliyunCloudStorageProperties.getAccessKeySecret());
        return ossClient;
    }

}
