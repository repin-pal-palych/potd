package com.repin.potd.service;

import com.repin.potd.model.PictureInfo;

import java.io.IOException;

/**
 * Picture of the day service interface.
 */
public interface PotdService {
    void updatePicture() throws IOException;
    PictureInfo getPicture() throws IOException;
}
