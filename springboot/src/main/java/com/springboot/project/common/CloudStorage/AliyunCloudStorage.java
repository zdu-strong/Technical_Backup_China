package com.springboot.project.common.CloudStorage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import org.apache.commons.lang3.ArrayUtils;
import org.jinq.orm.stream.JinqStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.ListObjectsV2Request;
import com.aliyun.oss.model.ListObjectsV2Result;
import com.aliyun.oss.model.OSSObjectSummary;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.springboot.project.common.storage.BaseStorage;
import com.springboot.project.common.storage.CloudStorageUrlResource;
import com.springboot.project.common.storage.RangeCloudStorageUrlResource;
import com.springboot.project.common.storage.SequenceResource;
import com.springboot.project.properties.AliyunCloudStorageProperties;
import io.reactivex.rxjava3.core.Observable;

@Component
public class AliyunCloudStorage extends BaseStorage implements CloudStorageInterface {

    private Duration tempUrlSurvivalDuration = Duration.ofDays(1);

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
                            key + this.getFileNameFromResource(
                                    new FileSystemResource(childOfSourceFileOrSourceFolder)));
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
            var list = this.getList(key + "/").toList().blockingGet();
            if (!list.isEmpty()) {
                JinqStream.from(list).where(s -> !s.equals(key + "/"))
                        .select(s -> this.getFileNameFromResource(new FileSystemResource(s)))
                        .select(s -> {
                            this.delete(key + "/" + s);
                            return "";
                        }).toList();
                ossClient.deleteObject(this.aliyunCloudStorageProperties.getBucketName(), key + "/");
            }
            ossClient.deleteObject(this.aliyunCloudStorageProperties.getBucketName(), key);
        } finally {
            ossClient.shutdown();
        }
    }

    @Override
    public Resource getResource(String key) {
        var list = this.getList(key + "/").toList().blockingGet();
        if (!list.isEmpty()) {
            try {
                var jsonString = new ObjectMapper()
                        .writeValueAsString(JinqStream.from(list).where(s -> !s.equals(key + "/"))
                                .select(s -> this.getFileNameFromResource(new FileSystemResource(s))).toList());
                var jsonBytes = jsonString.getBytes(StandardCharsets.UTF_8);
                return new ByteArrayResource(jsonBytes);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }

        var calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MILLISECOND, Long.valueOf(this.tempUrlSurvivalDuration.toMillis()).intValue());
        Date expireDate = calendar.getTime();

        var ossClient = this.getOssClientClient();
        try {
            var url = ossClient.generatePresignedUrl(this.aliyunCloudStorageProperties.getBucketName(), key,
                    expireDate);
            var getObjectRequest = new GetObjectRequest(this.aliyunCloudStorageProperties.getBucketName(),
                    key);
            var ossObject = ossClient.getObject(getObjectRequest);
            return new CloudStorageUrlResource(url, ossObject.getResponse().getContentLength());
        } finally {
            ossClient.shutdown();
        }
    }

    @Override
    public Resource getResource(String key, long startIndex, long rangeContentLength) {
        var list = this.getList(key + "/").toList().blockingGet();
        if (!list.isEmpty()) {
            try {
                var jsonString = new ObjectMapper()
                        .writeValueAsString(JinqStream.from(list).where(s -> !s.equals(key + "/"))
                                .select(s -> this.getFileNameFromResource(new FileSystemResource(s))).toList());
                var jsonBytes = jsonString.getBytes(StandardCharsets.UTF_8);
                var bytes = ArrayUtils.toPrimitive(
                        JinqStream.from(Lists.newArrayList(ArrayUtils.toObject(jsonBytes))).skip(startIndex)
                                .limit(rangeContentLength)
                                .toList().toArray(new Byte[] {}));
                return new ByteArrayResource(bytes);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }

        var calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MILLISECOND, Long.valueOf(this.tempUrlSurvivalDuration.toMillis()).intValue());
        Date expireDate = calendar.getTime();

        var ossClient = this.getOssClientClient();
        try {
            var url = ossClient.generatePresignedUrl(this.aliyunCloudStorageProperties.getBucketName(), key,
                    expireDate);
            return new RangeCloudStorageUrlResource(url, startIndex, rangeContentLength);
        } finally {
            ossClient.shutdown();
        }
    }

    @Override
    public Observable<String> getRootList() {
        return getList("").map(s -> this.getFileNameFromResource(new FileSystemResource(s)));
    }

    private Observable<String> getList(String prefix) {
        return Observable.create((emitter) -> {
            try {
                String nextContinuationToken = null;
                ListObjectsV2Result result = null;
                do {
                    if (emitter.isDisposed()) {
                        return;
                    }
                    ListObjectsV2Request listObjectsV2Request = new ListObjectsV2Request(
                            this.aliyunCloudStorageProperties.getBucketName()).withMaxKeys(200);
                    listObjectsV2Request.setPrefix(prefix);
                    listObjectsV2Request.setDelimiter("/");
                    listObjectsV2Request.setContinuationToken(nextContinuationToken);
                    var ossClient = this.getOssClientClient();
                    try {
                        result = ossClient.listObjectsV2(listObjectsV2Request);
                    } finally {
                        ossClient.shutdown();
                    }
                    for (OSSObjectSummary objectSummary : result.getObjectSummaries()) {
                        if (emitter.isDisposed()) {
                            return;
                        }
                        emitter.onNext(objectSummary.getKey());
                        Thread.sleep(0);
                    }

                    for (String commonPrefix : result.getCommonPrefixes()) {
                        if (emitter.isDisposed()) {
                            return;
                        }
                        emitter.onNext(commonPrefix);
                        Thread.sleep(0);
                    }

                    nextContinuationToken = result.getNextContinuationToken();
                } while (result.isTruncated());
                emitter.onComplete();
            } catch (Throwable e) {
                emitter.onError(e);
            }
        });
    }

    private OSS getOssClientClient() {
        var ossClient = new OSSClientBuilder().build(this.aliyunCloudStorageProperties.getEndpoint(),
                this.aliyunCloudStorageProperties.getAccessKeyId(),
                this.aliyunCloudStorageProperties.getAccessKeySecret());
        return ossClient;
    }

}
