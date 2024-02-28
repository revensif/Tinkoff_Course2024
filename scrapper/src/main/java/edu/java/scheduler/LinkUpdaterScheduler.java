package edu.java.scheduler;

import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class LinkUpdaterScheduler {

    @Scheduled(fixedDelayString = "#{@scheduler.interval()}")
    public void update() {
        log.info("Scheduler is running!");
    }
}
