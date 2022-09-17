package com.springboot.project.format;

import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.springboot.project.entity.LongTermTaskEntity;
import com.springboot.project.model.LongTermTaskModel;
import com.springboot.project.service.BaseService;

@Service
public class LongTermTaskFormatter extends BaseService {
    private Duration tempTaskSurvivalDuration = Duration.ofMinutes(1);

    public ResponseEntity<?> format(LongTermTaskEntity longTermTaskEntity) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MILLISECOND, Long.valueOf(0 - this.tempTaskSurvivalDuration.toMillis()).intValue());
        Date expireDate = calendar.getTime();
        if (!longTermTaskEntity.getIsDone() && longTermTaskEntity.getUpdateDate().before(expireDate)) {
            throw new RuntimeException("The task failed because it stopped");
        }

        var longTermTaskModel = new LongTermTaskModel<Object>().setId(longTermTaskEntity.getId())
                .setCreateDate(longTermTaskEntity.getCreateDate()).setUpdateDate(longTermTaskEntity.getUpdateDate())
                .setIsDone(longTermTaskEntity.getIsDone());

        if (longTermTaskEntity.getIsDone()) {
            JSONObject result = JSON.parseObject(longTermTaskEntity.getResult());
            longTermTaskModel.setResult(result.get("body"));

            HttpHeaders httpHeaders = new HttpHeaders();
            result.getJSONObject("headers").forEach((key, value) -> {
                httpHeaders.addAll(key,
                        Lists.newArrayList(result.getJSONObject("headers").getJSONArray(key).toJavaList(String.class)));
            });
            if (String.valueOf(result.getIntValue("statusCodeValue")).startsWith("2")) {
                ResponseEntity<LongTermTaskModel<?>> response = ResponseEntity
                        .status(result.getIntValue("statusCodeValue"))
                        .headers(httpHeaders).body(longTermTaskModel);
                return response;
            } else {
                ResponseEntity<?> response = ResponseEntity
                        .status(result.getIntValue("statusCodeValue"))
                        .headers(httpHeaders).body(result.get("body"));
                return response;
            }
        } else {
            return ResponseEntity.accepted().body(longTermTaskModel);
        }
    }
}
