package com.springboot.project.service;

import java.util.Date;
import org.springframework.stereotype.Service;
import com.fasterxml.uuid.Generators;
import com.springboot.project.entity.LoggerEntity;

@Service
public class LoggerService extends BaseService {

    public void addLogger(String message) {
        var loggerEntity = new LoggerEntity();
        loggerEntity.setId(Generators.timeBasedGenerator().generate().toString());
        loggerEntity.setCreateDate(new Date());
        loggerEntity.setMessage(message);
        this.entityManager.persist(loggerEntity);
    }

}
