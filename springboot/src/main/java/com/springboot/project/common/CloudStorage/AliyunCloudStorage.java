package com.springboot.project.common.CloudStorage;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'storageResource'");
    }

    @Override
    public void storageResource(SequenceResource sourceFile, String key) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'storageResource'");
    }

    @Override
    public void delete(String key) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
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

}
