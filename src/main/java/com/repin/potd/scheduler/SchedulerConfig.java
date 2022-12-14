package com.repin.potd.scheduler;

import com.repin.potd.service.CachingPotdService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;

/**
 * Configuration for scheduled tasks.
 */
@Configuration
@EnableScheduling
@Profile("!test")
public class SchedulerConfig {

    @Value("${api-key}")
    private String apiKey;
    @Value("${potd-base-url}")
    private String potdBaseUrl;
    @Value("${image-api-uri}")
    private String imageApiUri;

    @Bean
    public CachingPotdService cachingPotdService() {
        return new CachingPotdService(apiKey, potdBaseUrl, imageApiUri);
    }

    @Scheduled(cron = "0 0 10 * * ? *")
    public void pictureOfTheDayUpdater() throws IOException {
        cachingPotdService().updatePicture();
    }
}
