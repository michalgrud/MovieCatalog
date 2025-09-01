package com.michal.grud.movieCategorizationSystem.domain.movie.client.files;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface FilesStorage {
    void save(MultipartFile file, String fileId);

    InputStream load(String fileId);

    void delete(String fileId);
}
