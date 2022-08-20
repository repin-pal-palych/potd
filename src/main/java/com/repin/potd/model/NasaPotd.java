package com.repin.potd.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.net.URL;

@ParametersAreNonnullByDefault
public record NasaPotd(
        @JsonProperty("copyright") String copyright,
        @JsonProperty("date") String date,
        @JsonProperty("explanation") String explanation,
        @JsonProperty("hdurl") String hdUrl,
        @JsonProperty("media_type") String mediaType,
        @JsonProperty("service_version") String serviceVersion,
        @JsonProperty("title") String title,
        @JsonProperty("url") @Nonnull URL url
) {
}
