package com.springboot.project.controller;

import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.project.model.LongTermTaskModel;

@RestController
public class LongTermTaskController extends BaseController {
    private Duration tempWaitDuration = Duration.ofSeconds(30);

    /**
     * Because some requests take a long time to execute, so provide this
     * asynchronous task api. Call them first, like this: (
     * this.longTermTaskUtil.run(()->{});). And then call this api for polling to
     * obtain the execution results.
     * 
     * @param id
     * @return
     * @throws InterruptedException
     */
    @GetMapping("/long_term_task")
    public ResponseEntity<?> getLongTermTask(@RequestParam String id) throws InterruptedException {

        this.longTermTaskService
                .checkIsExistLongTermTaskById(this.encryptDecryptService.decryptByAES(id));

        Calendar calendarOfWait = Calendar.getInstance();
        calendarOfWait.setTime(new Date());
        calendarOfWait.add(Calendar.MILLISECOND, Long.valueOf(this.tempWaitDuration.toMillis()).intValue());
        Date expireDate = calendarOfWait.getTime();

        while (new Date().before(expireDate)) {
            var result = this.longTermTaskService
                    .getLongTermTask(this.encryptDecryptService.decryptByAES(id));
            if (result.getBody() instanceof LongTermTaskModel) {
                @SuppressWarnings("unchecked")
                final var longTermTaskResult = (ResponseEntity<LongTermTaskModel<?>>) result;
                if (longTermTaskResult.getBody().getIsDone()) {
                    return longTermTaskResult;
                }
            } else {
                return result;
            }
            Thread.sleep(1000);
        }

        return this.longTermTaskService
                .getLongTermTask(this.encryptDecryptService.decryptByAES(id));
    }
}
