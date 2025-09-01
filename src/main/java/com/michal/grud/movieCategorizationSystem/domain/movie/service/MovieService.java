package com.michal.grud.movieCategorizationSystem.domain.movie.service;

import com.michal.grud.movieCategorizationSystem.common.exception.GlobalValidationException;
import com.michal.grud.movieCategorizationSystem.common.util.VideoFileTypeChecker;
import com.michal.grud.movieCategorizationSystem.domain.movie.client.files.FilesStorage;
import com.michal.grud.movieCategorizationSystem.domain.movie.dto.MovieDetailsDTO;
import com.michal.grud.movieCategorizationSystem.domain.movie.entity.error.MovieErrorType;
import com.michal.grud.movieCategorizationSystem.domain.movie.entity.jpa.MovieEntity;
import com.michal.grud.movieCategorizationSystem.domain.movie.mapper.MovieMapper;
import com.michal.grud.movieCategorizationSystem.domain.movie.repository.MovieRepository;
import com.michal.grud.movieCategorizationSystem.domain.movie.service.vlidation.MovieValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRankScoreCalculator movieRankScoreCalculator;
    private final MovieRepository movieRepository;
    private final FilesStorage filesStorage;
    private final MovieValidator movieValidator;

    @Transactional
    public ResponseEntity<MovieDetailsDTO> createUpdateMovie(String title, String year, String director, MultipartFile file) {
        movieValidator.validateMovieCreateUpdate(file);
        String fileId = generateFileId(title, file);
        int rankScore = movieRankScoreCalculator.calculateRankScore(title, file.getSize());

        MovieEntity entity = MovieMapper.mapToMovieEntity(title, year, director, fileId, file.getSize(), rankScore);

        MovieEntity result = movieRepository.saveAndFlush(entity);

        filesStorage.save(file, fileId);

        return ResponseEntity.ok().body(MovieMapper.mapToMovieDetailsDTO(result));
    }

    @Transactional
    public ResponseEntity<Void> deleteMovie(String title) {
        MovieEntity entity = movieRepository.findById(title).orElseThrow(() -> new GlobalValidationException(MovieErrorType.MOVIE_NOT_FOUND.getDescription(),
                MovieErrorType.MOVIE_NOT_FOUND.name()));
        movieRepository.delete(entity);
        movieRepository.flush();
        filesStorage.delete(entity.getFileId());
        return ResponseEntity.ok().build();
    }

    public Page<MovieDetailsDTO> search(String sortBy, String dir, int page, int size) {
        if (!sortBy.equals("fileSize") && !sortBy.equals("rankScore")) {
            throw new IllegalArgumentException("sortBy must be fileSize or rankScore");
        }
        Sort sort = "asc".equalsIgnoreCase(dir)
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        return movieRepository.findAll(pageable).map(MovieMapper::mapToMovieDetailsDTO);
    }

    @Transactional
    public ResponseEntity<List<MovieDetailsDTO>> recalculateRankScore() {
        Collection<MovieEntity> entities = movieRepository.findAll();
        Collection<MovieEntity> updatedEntities = entities.stream().map(entity -> MovieMapper.mapToUpdatedMovieEntityWithRankScore(entity,
                movieRankScoreCalculator.calculateRankScore(entity.getTitle(),
                        entity.getFileSize()))).toList();
        Collection<MovieEntity> result = movieRepository.saveAll(updatedEntities);
        return ResponseEntity.ok(result.stream().map(MovieMapper::mapToMovieDetailsDTO).toList());
    }

    private String generateFileId(String title, MultipartFile file) {
        String fileExtension = VideoFileTypeChecker.getVideoExtension(file);
        return String.format("%s.%s", title, fileExtension);
    }

    public ResponseEntity<StreamingResponseBody> downloadMovie(String title) {
        MovieEntity entity = movieRepository.findById(title).orElseThrow(() -> new GlobalValidationException(MovieErrorType.MOVIE_NOT_FOUND.getDescription(),
                MovieErrorType.MOVIE_NOT_FOUND.name()));
        InputStream in = filesStorage.load(entity.getFileId());

        ContentDisposition cd = ContentDisposition
                .attachment()
                .filename(entity.getFileId(), StandardCharsets.UTF_8)
                .build();

        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;

        StreamingResponseBody body = out -> {
            try (in) {
                in.transferTo(out);
            }
        };

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, cd.toString())
                .header("X-Content-Type-Options", "nosniff")
                .contentType(mediaType)
                .body(body);
    }

}