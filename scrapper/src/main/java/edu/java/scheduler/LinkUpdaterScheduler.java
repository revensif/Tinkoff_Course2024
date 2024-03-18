package edu.java.scheduler;

import edu.java.service.LinkUpdater;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class LinkUpdaterScheduler {

    private final LinkUpdater linkUpdater;

    public LinkUpdaterScheduler(@Qualifier("jdbcLinkUpdater") LinkUpdater linkUpdater) {
        this.linkUpdater = linkUpdater;
    }

    @Scheduled(fixedDelayString = "#{@scheduler.interval()}")
    public void update() {
        linkUpdater.update();
    }
}
