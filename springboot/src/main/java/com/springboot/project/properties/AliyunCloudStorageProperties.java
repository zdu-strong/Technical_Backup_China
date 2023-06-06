package com.springboot.project.properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties
@PropertySource("classpath:application.yml")
public class AliyunCloudStorageProperties {
    @Autowired
    private Environment environment;

    @Value("${properties.storage.cloud.aliyun.enabled}")
    private Boolean enabled;

    @Value("${properties.storage.cloud.aliyun.endpoint}")
    private String endpoint;

    @Value("${properties.storage.cloud.aliyun.bucketName}")
    private String bucketName;

    @Value("${properties.storage.cloud.aliyun.accessKeyId}")
    private String accessKeyId;

    @Value("${properties.storage.cloud.aliyun.accessKeySecret}")
    private String accessKeySecret;

    public Boolean getEnabled() {
        return Boolean.valueOf(
                this.environment.getProperty("PROPERTIES_STORAGE_CLOUD_ALIYUN_ENABLED", String.valueOf(this.enabled)));
    }

    public String getEndpoint() {
        return this.environment.getProperty("PROPERTIES_STORAGE_CLOUD_ALIYUN_ENDPOINT", this.endpoint);
    }

    public String getBucketName() {
        return this.environment.getProperty("PROPERTIES_STORAGE_CLOUD_ALIYUN_BUCKET_NAME", this.bucketName);
    }

    public String getAccessKeyId() {
        return this.environment.getProperty("PROPERTIES_STORAGE_CLOUD_ALIYUN_ACCESS_KEY_ID", this.accessKeyId);
    }

    public String getAccessKeySecret() {
        return this.environment.getProperty("PROPERTIES_STORAGE_CLOUD_ALIYUN_ACCESS_KEY_SECRET", this.accessKeySecret);
    }
}
