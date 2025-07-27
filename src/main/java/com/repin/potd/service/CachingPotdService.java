package com.repin.potd.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.repin.potd.model.NasaPotd;
import com.repin.potd.model.PictureInfo;
import org.slf4j.Logger;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;

import static com.repin.potd.exception.ExceptionUtils.unchecked;
import static java.time.format.DateTimeFormatter.ISO_DATE;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static reactor.core.publisher.Mono.empty;
import static reactor.core.publisher.Mono.fromCallable;

/**
 * Picture of the day service implementation.
 */
@ParametersAreNonnullByDefault
public class CachingPotdService implements PotdService {

    private static final Logger LOG = getLogger(CachingPotdService.class);

    private static final String PICTURE_FORMAT_JPG = "jpg";
    private final String imageApiUri;
    private final String apiKey;
    private final WebClient client;

    private NasaPotd pictureOfTheDay;
    private PictureInfo pictureInfo;
    private Cache<String, String> fileIdCache;

    public CachingPotdService(
        String apiKey,
        String potdBaseUrl,
        String imageApiUri,
        Cache<String, String> fileIdCache
    ) {
        LOG.info("Init CachingPotdService...");
        this.apiKey = apiKey;
        this.imageApiUri = imageApiUri;
        this.client = WebClient.create(potdBaseUrl);
        this.fileIdCache = fileIdCache;
    }

    /**
     * Get picture of the day by NASA.
     *
     * @return object with image data - sd/hd images, description, title, copyright etc...
     */
    @Override
    public PictureInfo getPicture() {
        if (pictureOfTheDay == null) updatePicture();
        return pictureInfo;
    }

    /**
     * Update picture of the day image information.
     */
    @Override
    public void updatePicture() {
        final var date = ISO_DATE.format(LocalDate.now());
        LOG.info("Updating picture of the day, date: {}", date);
        final var updatedPotd = getNasaPotdDto(date);
        if (!updatedPotd.equals(pictureOfTheDay)) {
            pictureOfTheDay = updatedPotd;
            updatePictureInfo(date);
            fileIdCache.invalidate(date);
        }
    }

    private void updatePictureInfo(String date) {
        final var image = getImageByUrl(pictureOfTheDay.url());
        ByteArrayOutputStream imageBaos = new ByteArrayOutputStream();
        ByteArrayOutputStream hdImageBaos = new ByteArrayOutputStream();
        final var maybeHdImage = pictureOfTheDay.hdUrl().map(this::getImageByUrl);
        writeImageToOutputStream(image, imageBaos);
        maybeHdImage.ifPresent(hdImage -> writeImageToOutputStream(hdImage, hdImageBaos));
        pictureInfo = new PictureInfo(
            imageBaos,
            pictureOfTheDay.title(),
            pictureOfTheDay.explanation(),
            pictureOfTheDay.copyright(),
            maybeHdImage.isPresent() ? Optional.of(hdImageBaos) : Optional.empty()
        );
    }

    private static boolean writeImageToOutputStream(BufferedImage image, ByteArrayOutputStream outputStream) {
        return unchecked(() -> ImageIO.write(image, PICTURE_FORMAT_JPG, outputStream));
    }

    public NasaPotd getNasaPotdDto(String date) {
        return getNasaPotdInternal(date, true);
    }

    private NasaPotd getNasaPotdInternal(String date, boolean shouldRetry) {
        return Optional.ofNullable(client.get()
            .uri(uriBuilder -> uriBuilder
                .path(imageApiUri)
                .queryParam("api_key", apiKey)
                .queryParam("date", date)
                .build())
            .accept(APPLICATION_JSON)
            .retrieve()
            .onStatus(status -> status.value() == 400 && shouldRetry, response -> {
                return response.bodyToMono(String.class)
                    .flatMap(body -> fromCallable(() -> getNasaPotdInternal("", false)))
                    .then(empty());
            })
            .bodyToMono(NasaPotd.class)
            .block()).orElseThrow(() -> new RuntimeException("Empty response"));
    }

    private BufferedImage getImageByUrl(URL url) {
        return unchecked(() -> ImageIO.read(url));
    }
}
