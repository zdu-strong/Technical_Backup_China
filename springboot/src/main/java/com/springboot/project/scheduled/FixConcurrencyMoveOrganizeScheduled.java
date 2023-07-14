package com.springboot.project.scheduled;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.springboot.project.service.OrganizeService;
import lombok.extern.slf4j.Slf4j;

@Component
@EnableScheduling
@Slf4j
public class FixConcurrencyMoveOrganizeScheduled {

    @Autowired
    private OrganizeService organizeService;

    @Scheduled(initialDelay = 1000, fixedDelay = 1000)
    public void scheduled() {
        try {
            this.organizeService.fixConcurrencyMoveOrganize();
        } catch (Throwable e) {
            log.error("Failed to fix concurrency move organize", e);
        }
    }
}
