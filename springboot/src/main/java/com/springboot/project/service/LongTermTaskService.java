package com.springboot.project.service;

import java.util.Date;
import com.fasterxml.uuid.Generators;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.alibaba.fastjson.JSON;
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

        this.entityManager.persist(longTermTaskEntity);
        return longTermTaskEntity.getId();
    }

    public void updateLongTermTaskToRefreshUpdateDate(String id) {
        LongTermTaskEntity longTermTaskEntity = this.LongTermTaskEntity().where(s -> s.getId().equals(id))
                .getOnlyValue();
        longTermTaskEntity.setUpdateDate(new Date());
        this.entityManager.merge(longTermTaskEntity);
    }

    public void updateLongTermTaskByResult(String id, ResponseEntity<?> result) {
        LongTermTaskEntity longTermTaskEntity = this.LongTermTaskEntity().where(s -> s.getId().equals(id))
                .getOnlyValue();
        longTermTaskEntity.setUpdateDate(new Date());
        longTermTaskEntity.setIsDone(true);
        longTermTaskEntity.setResult(JSON.toJSONString(result));
        this.entityManager.merge(longTermTaskEntity);
    }

    public void updateLongTermTaskByErrorMessage(String id, Throwable e) {
        LongTermTaskEntity longTermTaskEntity = this.LongTermTaskEntity().where(s -> s.getId().equals(id))
                .getOnlyValue();
        longTermTaskEntity.setIsDone(true);
        longTermTaskEntity.setUpdateDate(new Date());
        if (e instanceof ResponseStatusException) {
            var body = JSON.parseObject(JSON.toJSONString(e));
            if (((ResponseStatusException) e).getReason() != null) {
                body.put("message", ((ResponseStatusException) e).getReason());
            }
            longTermTaskEntity.setResult(
                    JSON.toJSONString(ResponseEntity.status(((ResponseStatusException) e).getStatus())
                            .body(body)));
        } else {
            longTermTaskEntity.setResult(JSON.toJSONString(ResponseEntity.internalServerError()
                    .body(JSON.parseObject(JSON.toJSONString(e)))));
        }
        this.entityManager.merge(longTermTaskEntity);
    }

    public ResponseEntity<?> getLongTermTask(String id) {
        LongTermTaskEntity longTermTaskEntity = this.LongTermTaskEntity().where(s -> s.getId().equals(id))
                .getOnlyValue();
        return this.longTermTaskFormatter.format(longTermTaskEntity);
    }

    public void checkIsExistLongTermTaskById(String id) {
        var isExistLongTermTask = this.LongTermTaskEntity().where(s -> s.getId().equals(id)).findFirst().isPresent();
        if (!isExistLongTermTask) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The specified task does not exist");
        }
    }

}
