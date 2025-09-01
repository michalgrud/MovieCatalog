package com.michal.grud.movieCategorizationSystem.domain.movie.service.vlidation;

import com.michal.grud.movieCategorizationSystem.common.exception.GlobalValidationException;
import com.michal.grud.movieCategorizationSystem.domain.movie.entity.error.MovieErrorType;
import com.michal.grud.movieCategorizationSystem.domain.movie.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class MovieValidator {

    private static final long ONE_GB = 1L * 1024 * 1024 * 1024;
    private final MovieRepository movieRepository;

    public void validateMovieCreateUpdate(MultipartFile file) {
        validateFileSize(file);
    }

    private void validateFileSize(MultipartFile file) {
        if (file != null && file.getSize() > ONE_GB) {
            throw new GlobalValidationException(MovieErrorType.TOO_LARGE_FILE.name(), MovieErrorType.TOO_LARGE_FILE.getDescription());
        }
    }

    public void validateTitleExistence(String title) {
        movieRepository.findById(title).ifPresent(movie -> {
            throw new GlobalValidationException(MovieErrorType.MOVIE_ALREADY_EXISTS.name(),
                    MovieErrorType.MOVIE_ALREADY_EXISTS.getDescription());
        });
    }
}
