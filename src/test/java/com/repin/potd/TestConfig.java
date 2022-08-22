package com.repin.potd;

import com.repin.potd.service.CachingPotdService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
public class TestConfig {

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
}
