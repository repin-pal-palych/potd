package com.repin.potd.scheduler;

import com.repin.potd.service.PotdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;

/**
 * Configuration for scheduled tasks.
 */
@Configuration
@EnableScheduling
public class SchedulerConfig {

    @Autowired
    PotdService potdService;

    @Scheduled(cron = "0 0 10 * * ? *")
    public void pictureOfTheDayUpdater() throws IOException {
        potdService.updatePicture();
    }
}
