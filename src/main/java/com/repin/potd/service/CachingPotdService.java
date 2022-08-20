package com.repin.potd.service;

import com.repin.potd.model.NasaPotd;
import org.jvnet.hk2.annotations.Service;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

@Service
@ParametersAreNonnullByDefault
public class CachingPotdService implements PotdService {

    private final String imageApiUri;
    private final String apiKey;
    private final WebClient client;
    private BufferedImage image;
    private URL potdUrl;

    public CachingPotdService(String imageApiUri, String apiKey, WebClient client) {
        this.imageApiUri = imageApiUri;
        this.apiKey = apiKey;
        this.client = client;
    }

    @Override
    public void updatePicture() throws IOException {
        potdUrl = getPotdUrl();
        image = ImageIO.read(potdUrl);
    }

    @Override
    public byte[] getPicture() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        if (image == null) updatePicture();
        ImageIO.write(image, "jpg", outputStream);
        return outputStream.toByteArray();
    }

    @Nonnull
    private URL getPotdUrl() {
        var nasaPotd = Optional.ofNullable(client.get()
                .uri(uriBuilder -> uriBuilder
                        .path(imageApiUri)
                        .queryParam("api_key", apiKey)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(NasaPotd.class)
                .block()).orElseThrow(() -> new RuntimeException("Empty response"));
        potdUrl = nasaPotd.url();
        return potdUrl;
    }
}
