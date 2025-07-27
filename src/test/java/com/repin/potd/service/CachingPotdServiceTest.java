package com.repin.potd.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.repin.potd.model.PictureInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(MockitoExtension.class)
class CachingPotdServiceTest {

    @Mock
    Cache<String, String> fileIdCache;

    @InjectMocks
    CachingPotdService service;

    private WebClient webClient;

    @BeforeEach
    void setUp() {
        // Используем WebClient с мокнутым ответом
        webClient = WebClient.builder()
            .exchangeFunction(clientRequest -> {
                ClientResponse response = ClientResponse.create(OK)
                    .header("Content-Type", "application/json")
                    .body("""
                        {
                          "url": "file:src/test/resources/baby yoda.jpg",
                          "hdurl": "file:src/test/resources/baby yoda.jpg",
                          "title": "Test Image",
                          "explanation": "This is a test image",
                          "copyright": "NASA"
                        }
                        """)
                    .build();
                return Mono.just(response);
            })
            .build();

        service = new CachingPotdService(
            "dummy-api-key",
            "/planetary/apod",
            webClient,
            fileIdCache
        );
    }

    @Test
    void shouldFetchAndCachePotd() {
        service.updatePicture();

        PictureInfo result = service.getPicture();

        assertNotNull(result);
        assertEquals("Test Image", result.title());
        assertEquals("This is a test image", result.description());
        verify(fileIdCache).invalidate(anyString());
    }
}