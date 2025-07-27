package com.repin.potd.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.net.URL;
import java.util.Optional;

@ParametersAreNonnullByDefault
public record NasaPotd(
        @JsonProperty("url") @Nonnull URL url,
        @JsonProperty("copyright") String copyright,
        @JsonProperty("date") String date,
        @JsonProperty("explanation") String explanation,
        @JsonProperty("media_type") String mediaType,
        @JsonProperty("service_version") String serviceVersion,
        @JsonProperty("title") String title,
        @JsonProperty("hdurl") Optional<URL> hdUrl
        ) {
}
