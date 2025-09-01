package com.michal.grud.movieCategorizationSystem.domain.movie.entity.error;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class MovieErrorTypeTest {

    @Test
    void shouldExposeExactDescriptions() {
        assertThat(MovieErrorType.MOVIE_NOT_FOUND.getDescription())
                .isEqualTo("Movie not found");
        assertThat(MovieErrorType.MOVIE_ALREADY_EXISTS.getDescription())
                .isEqualTo("Movie already exists");
        assertThat(MovieErrorType.TOO_LARGE_FILE.getDescription())
                .isEqualTo("File is too large, Should be less than 1GB");
    }

    @ParameterizedTest
    @EnumSource(MovieErrorType.class)
    void descriptionShouldNotBeNullOrBlank(MovieErrorType type) {
        assertThat(type.getDescription()).isNotNull().isNotBlank();
    }

    @Test
    void valueOfShouldReturnProperConstant() {
        assertThat(MovieErrorType.valueOf("MOVIE_NOT_FOUND"))
                .isSameAs(MovieErrorType.MOVIE_NOT_FOUND);
    }

    @Test
    void enumShouldContainExactlyExpectedValues() {
        assertThat(MovieErrorType.values())
                .containsExactly(
                        MovieErrorType.MOVIE_NOT_FOUND,
                        MovieErrorType.MOVIE_ALREADY_EXISTS,
                        MovieErrorType.TOO_LARGE_FILE
                );
    }

    @Test
    void toStringShouldEqualNameByDefault() {
        assertThat(MovieErrorType.MOVIE_ALREADY_EXISTS.toString())
                .isEqualTo(MovieErrorType.MOVIE_ALREADY_EXISTS.name());
    }
}