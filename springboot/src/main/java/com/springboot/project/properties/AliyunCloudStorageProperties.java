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

    @Value("${properties.CloudStorage.aliyun.enabled}")
    private Boolean enabled;

    public Boolean getEnabled() {
        return Boolean.valueOf(
                this.environment.getProperty("PROPERTIES_CLOUD_STORAGE_ALIYUN_ENABLED", String.valueOf(this.enabled)));
    }
}
