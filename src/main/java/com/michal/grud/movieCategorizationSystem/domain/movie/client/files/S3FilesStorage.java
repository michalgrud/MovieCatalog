package com.michal.grud.movieCategorizationSystem.domain.movie.client.files;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Component
@Profile("prod")
public class S3FilesStorage implements FilesStorage {


    @Override
    public void save(MultipartFile file, String fileId) {
        throw new UnsupportedOperationException("S3 support will be added in the feature");
    }

    @Override
    public InputStream load(String fileId) {
        throw new UnsupportedOperationException("S3 support will be added in the feature");
    }

    @Override
    public void delete(String fileId) {
        throw new UnsupportedOperationException("S3 support will be added in the feature");
    }
}
