package com.repin.potd.service;

import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FileIdService {

    private final Cache<String, String> fileIdCache;

    public FileIdService(Cache<String, String> fileIdCache) {
        this.fileIdCache = fileIdCache;
    }

    public void saveFileId(String imageKey, String fileId) {
        fileIdCache.put(imageKey, fileId);
    }

    public Optional<String> findFileId(String imageKey) {
        return Optional.ofNullable(fileIdCache.getIfPresent(imageKey));
    }
}