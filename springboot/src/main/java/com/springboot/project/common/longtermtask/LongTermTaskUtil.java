package com.springboot.project.common.longtermtask;

import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import com.springboot.project.service.EncryptDecryptService;
import com.springboot.project.service.LongTermTaskService;
import io.reactivex.rxjava3.core.Observable;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class LongTermTaskUtil {
    @Autowired
    private LongTermTaskService longTermTaskService;

    @Autowired
    private EncryptDecryptService encryptDecryptService;

    /**
     * The return value of the executed method will be stored in the database as a
     * json string, and will be converted into a json object or json object array
     * and returned after success. This method will return a relative url, can call
     * a get request to get the result.
     * 
     * @param runnable
     * @return
     */
    public ResponseEntity<String> run(Supplier<ResponseEntity<?>> supplier) {
        String idOfLongTermTask = this.longTermTaskService.createLongTermTask();
        CompletableFuture.runAsync(() -> {
            var subscription = Observable.timer(1, TimeUnit.SECONDS).concatMap((a) -> {
                synchronized (idOfLongTermTask) {
                    CompletableFuture.runAsync(() -> {
                        this.longTermTaskService.updateLongTermTaskToRefreshUpdateDate(idOfLongTermTask);
                    }).get();
                }
                return Observable.empty();
            }).repeat().retry().subscribe();
            try {
                var result = CompletableFuture.supplyAsync(() -> supplier.get()).get();
                subscription.dispose();
                synchronized (idOfLongTermTask) {
                    CompletableFuture.runAsync(() -> {
                        this.longTermTaskService.updateLongTermTaskByResult(idOfLongTermTask, result);
                    }).get();
                }
            } catch (Throwable e) {
                subscription.dispose();
                synchronized (idOfLongTermTask) {
                    try {
                        CompletableFuture.runAsync(() -> {
                            if (e instanceof ExecutionException && e.getCause() != null) {
                                this.longTermTaskService.updateLongTermTaskByErrorMessage(idOfLongTermTask,
                                        ((ExecutionException) e).getCause());
                            } else {
                                this.longTermTaskService.updateLongTermTaskByErrorMessage(idOfLongTermTask, e);
                            }
                        }).get();
                    } catch (InterruptedException | ExecutionException e4) {
                        log.error("Failed to saving exception message for long term task \"" + idOfLongTermTask + "\"", e4);
                    }
                }
            }
        });
        try {
            var url = new URIBuilder("/long_term_task").setParameter("id",
                    this.encryptDecryptService.encryptByAES(idOfLongTermTask)).build();
            return ResponseEntity.accepted().body(url.toString());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
