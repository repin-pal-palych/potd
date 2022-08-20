package com.repin.potd.service;

import java.io.IOException;

/**
 * Picture of the day service interface.
 */
public interface PotdService {
    long updatePicture() throws IOException;
    byte[] getPicture() throws IOException;
}
