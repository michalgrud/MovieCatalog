package com.michal.grud.movieCategorizationSystem.domain.movie.client.files;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

@Component
@Profile({"local", "test"})
public class LocalFileStorage implements FilesStorage {

    private final Path rootLocation;

    public LocalFileStorage(@Value("${storage.local.root}") String root) {
        this.rootLocation = Paths.get(root).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage location " + rootLocation, e);
        }
    }

    @Override
    public void save(MultipartFile file, String fileId) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Cannot store empty file");
        }
        if (fileId == null || fileId.isBlank()) {
            throw new IllegalArgumentException("fileId must not be blank");
        }

        try {
            Path destinationFile = this.rootLocation.resolve(fileId).normalize().toAbsolutePath();

            if (!destinationFile.startsWith(this.rootLocation)) {
                throw new SecurityException("Cannot store file outside root location");
            }

            Path parentDir = destinationFile.getParent();
            if (parentDir != null && !Files.exists(parentDir)) {
                Files.createDirectories(parentDir);
            }

            try (InputStream in = file.getInputStream()) {
                Files.copy(in, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to store file " + fileId, e);
        }
    }

    @Override
    public InputStream load(String fileId) {
        try {
            Path file = rootLocation.resolve(fileId).normalize().toAbsolutePath();

            if (!file.startsWith(rootLocation)) {
                throw new SecurityException("Cannot access outside root location");
            }
            if (!Files.exists(file)) {
                throw new NoSuchFileException("File not found: " + file);
            }
            return Files.newInputStream(file, StandardOpenOption.READ);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load file " + fileId, e);
        }
    }

    @Override
    public void delete(String fileId) {
        if (fileId == null || fileId.isBlank()) {
            throw new IllegalArgumentException("fileId must not be blank");
        }
        try {
            Path file = rootLocation.resolve(fileId).normalize().toAbsolutePath();

            if (!file.startsWith(rootLocation)) {
                throw new SecurityException("Cannot delete outside root location");
            }

            boolean deleted = Files.deleteIfExists(file);
            if (!deleted) {
                throw new NoSuchFileException("File not found: " + file);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file " + fileId, e);
        }
    }
}