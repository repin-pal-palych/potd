package com.repin.potd.service;

import com.google.common.annotations.VisibleForTesting;
import com.repin.potd.model.NasaPotd;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;

/**
 * Picture of the day service implementation.
 */
@ParametersAreNonnullByDefault
public class CachingPotdService implements PotdService {

    private static final String PICTURE_FORMAT_JPG = "jpg";
    private final String imageApiUri;
    private final String apiKey;
    private final WebClient client;
    private BufferedImage image;

    private ByteArrayOutputStream outputStream;

    public CachingPotdService(
            String apiKey,
            String potdBaseUrl,
            String imageApiUri
    ) {
        this.apiKey = apiKey;
        this.imageApiUri = imageApiUri;
        this.client = WebClient.create(potdBaseUrl);
    }

    /**
     * Get picture of the day by NASA.
     * @return  byte array with picture data in jpg format.
     */
    @Override
    public byte[] getPicture() throws IOException {
        if (image == null) updatePicture();
        return outputStream != null
                ? outputStream.toByteArray()
                : null;
    }

    /**
     * Update picture of the day image file.
     *
     * @return size of picture (in pixels). If size is 0 then picture is empty.
     */
    @Override
    public long updatePicture() throws IOException {
        long size = updateImageFile(getPotdUrl());
        updateOutputBuffer();
        return size;
    }

    private void updateOutputBuffer() throws IOException {
        outputStream = new ByteArrayOutputStream();
        writeImageToOutputStream();
    }

    private void writeImageToOutputStream() throws IOException {
        ImageIO.write(image, PICTURE_FORMAT_JPG, outputStream);
    }

    @VisibleForTesting
    long updateImageFile(URL url) throws IOException {
        image = ImageIO.read(url);
        return image != null
                // possible int overload
                ? (long) image.getWidth() * image.getHeight()
                : 0;
    }

    /**
     * Requests JSON with information about photo of the day.
     * @return  photo of the day URL.
     */
    @Nonnull
    @VisibleForTesting
    URL getPotdUrl() {
        var nasaPotd = getNasaPotdDto();
        return nasaPotd.url();
    }

    @VisibleForTesting
    NasaPotd getNasaPotdDto() {
        return Optional.ofNullable(client.get()
                .uri(uriBuilder -> uriBuilder
                        .path(imageApiUri)
                        .queryParam("api_key", apiKey)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(NasaPotd.class)
                .block()).orElseThrow(() -> new RuntimeException("Empty response"));
    }
}
