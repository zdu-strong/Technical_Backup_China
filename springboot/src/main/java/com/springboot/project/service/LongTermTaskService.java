package com.springboot.project.service;

import java.util.Date;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.uuid.Generators;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.springboot.project.entity.LongTermTaskEntity;

@Service
public class LongTermTaskService extends BaseService {
    public String createLongTermTask() {
        LongTermTaskEntity longTermTaskEntity = new LongTermTaskEntity();
        longTermTaskEntity.setId(Generators.timeBasedGenerator().generate().toString());
        longTermTaskEntity.setCreateDate(new Date());
        longTermTaskEntity.setUpdateDate(new Date());
        longTermTaskEntity.setIsDone(false);
        longTermTaskEntity.setResult(null);

        this.persist(longTermTaskEntity);
        return longTermTaskEntity.getId();
    }

    public void updateLongTermTaskToRefreshUpdateDate(String id) {
        LongTermTaskEntity longTermTaskEntity = this.LongTermTaskEntity().where(s -> s.getId().equals(id))
                .getOnlyValue();
        if (longTermTaskEntity.getIsDone()) {
            return;
        }
        longTermTaskEntity.setUpdateDate(new Date());
        this.merge(longTermTaskEntity);
    }

    public void updateLongTermTaskByResult(String id, ResponseEntity<?> result) {
        try {
            LongTermTaskEntity longTermTaskEntity = this.LongTermTaskEntity().where(s -> s.getId().equals(id))
                    .getOnlyValue();
            longTermTaskEntity.setUpdateDate(new Date());
            longTermTaskEntity.setIsDone(true);
            longTermTaskEntity.setResult(new ObjectMapper().writeValueAsString(result));
            this.merge(longTermTaskEntity);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void updateLongTermTaskByErrorMessage(String id, Throwable e) {
        try {
            LongTermTaskEntity longTermTaskEntity = this.LongTermTaskEntity().where(s -> s.getId().equals(id))
                    .getOnlyValue();
            longTermTaskEntity.setIsDone(true);
            longTermTaskEntity.setUpdateDate(new Date());
            if (e instanceof ResponseStatusException) {
                var body = new ObjectMapper().readTree(new ObjectMapper()
                        .configure(SerializationFeature.WRITE_SELF_REFERENCES_AS_NULL, true)
                        .configure(SerializationFeature.FAIL_ON_SELF_REFERENCES, false).writeValueAsString(e));
                if (((ResponseStatusException) e).getReason() != null) {
                    body.withObject("").put("message", ((ResponseStatusException) e).getReason());
                }
                longTermTaskEntity.setResult(
                        new ObjectMapper()
                                .writeValueAsString(ResponseEntity.status(((ResponseStatusException) e).getStatusCode())
                                        .body(body)));
            } else {
                longTermTaskEntity.setResult(new ObjectMapper().writeValueAsString(ResponseEntity.internalServerError()
                        .body(new ObjectMapper().readTree(new ObjectMapper()
                                .configure(SerializationFeature.WRITE_SELF_REFERENCES_AS_NULL, true)
                                .configure(SerializationFeature.FAIL_ON_SELF_REFERENCES, false)
                                .writeValueAsString(e)))));
            }
            this.merge(longTermTaskEntity);
        } catch (JsonProcessingException e1) {
            throw new RuntimeException(e1.getMessage(), e1);
        }
    }

    public ResponseEntity<?> getLongTermTask(String id) {
        LongTermTaskEntity longTermTaskEntity = this.LongTermTaskEntity().where(s -> s.getId().equals(id))
                .getOnlyValue();
        return this.longTermTaskFormatter.format(longTermTaskEntity);
    }

    public void checkIsExistLongTermTaskById(String id) {
        var isExistLongTermTask = this.LongTermTaskEntity().where(s -> s.getId().equals(id)).exists();
        if (!isExistLongTermTask) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The specified task does not exist");
        }
    }

}
