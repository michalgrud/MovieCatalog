package com.michal.grud.movieCategorizationSystem.domain.movie.client.digicat;

import com.michal.grud.movieCategorizationSystem.domain.movie.client.digikat.DigiKatErrorType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class DigiKatErrorTypeTest {
    @Test
    void shouldExposeExactDescriptionForMovieNotFound() {
        assertThat(DigiKatErrorType.DIGIKAT_MOVIE_NOT_FOUND.getDescription())
                .isEqualTo("Movie not found in DigiKat");
    }

    @ParameterizedTest
    @EnumSource(DigiKatErrorType.class)
    void descriptionShouldNotBeNullOrBlank(DigiKatErrorType type) {
        assertThat(type.getDescription()).isNotNull().isNotBlank();
    }

    @Test
    void valueOfShouldReturnProperConstant() {
        assertThat(DigiKatErrorType.valueOf("DIGIKAT_MOVIE_NOT_FOUND"))
                .isSameAs(DigiKatErrorType.DIGIKAT_MOVIE_NOT_FOUND);
    }

    @Test
    void enumShouldContainExactlyExpectedValues() {
        assertThat(DigiKatErrorType.values())
                .containsExactly(DigiKatErrorType.DIGIKAT_MOVIE_NOT_FOUND);
    }

    @Test
    void toStringShouldEqualNameByDefault() {
        assertThat(DigiKatErrorType.DIGIKAT_MOVIE_NOT_FOUND.toString())
                .isEqualTo(DigiKatErrorType.DIGIKAT_MOVIE_NOT_FOUND.name());
    }
}
