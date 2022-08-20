package com.repin.potd.service;

import java.io.IOException;

/**
 * Сервис для получения фотографии дня.
 */
public interface PotdService {
    void updatePicture() throws IOException;
    byte[] getPicture() throws IOException;
}
