package com.repin.potd.model;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.ByteArrayOutputStream;
import java.util.Optional;

@ParametersAreNonnullByDefault
public record PictureInfo(
    ByteArrayOutputStream image,
    String title,
    String description,
    String copyright,
    Optional<ByteArrayOutputStream> hdImage
    ) {
}
