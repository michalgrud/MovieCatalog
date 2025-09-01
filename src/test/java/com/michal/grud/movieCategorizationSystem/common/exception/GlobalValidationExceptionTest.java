package com.michal.grud.movieCategorizationSystem.common.exception;

import com.michal.grud.movieCategorizationSystem.domain.movie.entity.error.MovieErrorType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class GlobalValidationExceptionTest {
    @Test
    void shouldExposeCodeAndDescription() {
        GlobalValidationException ex = new GlobalValidationException(MovieErrorType.MOVIE_NOT_FOUND.name(), MovieErrorType.MOVIE_NOT_FOUND.getDescription());
        assertThat(ex.getErrorCode()).isEqualTo(MovieErrorType.MOVIE_NOT_FOUND.name());
        assertThat(ex.getDescription()).isEqualTo(MovieErrorType.MOVIE_NOT_FOUND.getDescription());
        assertThat(ex).hasMessage(MovieErrorType.MOVIE_NOT_FOUND.getDescription());
    }
}