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
public class StorageRootPathProperties {

    @Autowired
    private Environment environment;

    @Value("${properties.StorageRootPath}")
    private String storageRootPath;

    public boolean isTestEnviroment() {
        var signalOfTestEnv = "defaultTest-a56b075f-102e-edf3-8599-ffc526ec948a";
        if (signalOfTestEnv.equals(this.getStorageRootPath())) {
            return true;
        } else {
            return false;
        }
    }

    public String getStorageRootPath() {
        return this.environment.getProperty("PROPERTIES_STORAGE_ROOT_PATH", this.storageRootPath);
    }
}
