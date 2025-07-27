package com.repin.potd.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

class CachingPotdServiceTest extends FunctionalTest {

    public static final String IMAGE_FILE_PATH = "src/test/resources/baby yoda.jpg";

    @Autowired
    private CachingPotdService cachingPotdService;

    @Test
    @DisplayName("Проверка основного флоу обновления картинки.")
    void shouldUpdateImage() throws IOException {
        CachingPotdService spyService = Mockito.spy(cachingPotdService);
        doAnswer(invocation -> "someUrl").when(spyService).getPotdUrl();
        doAnswer(invocation -> 10_000L).when(spyService).updatePicture();
        var size = spyService.updatePicture();
        Mockito.verify(spyService, Mockito.times(1)).updatePicture();
        assertEquals(10_000L, size);
    }

    @Test
    @DisplayName("Получаем картинку.")
    void shouldGetPicture() throws IOException, NoSuchFieldException, IllegalAccessException {
        File input = new File(IMAGE_FILE_PATH);
        BufferedImage babyYodaImage = ImageIO.read(input);
        Field imageField = cachingPotdService.getClass().getDeclaredField("image");
        imageField.setAccessible(true);
        imageField.set(cachingPotdService, babyYodaImage);
        CachingPotdService spyService = Mockito.spy(cachingPotdService);

        doAnswer(invocation -> 10_000L).when(spyService).updateImageFile(any());
        doAnswer(invocation -> null).when(spyService).getPotdUrl();

        spyService.updatePicture();
        byte[] picture = spyService.getPicture();
        assertNotNull(picture);
        Mockito.verify(spyService, Mockito.times(1)).updatePicture();
    }
}