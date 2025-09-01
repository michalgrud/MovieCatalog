package com.michal.grud.movieCategorizationSystem.domain.movie.service.validation;

import com.michal.grud.movieCategorizationSystem.common.exception.GlobalValidationException;
import com.michal.grud.movieCategorizationSystem.domain.movie.entity.error.MovieErrorType;
import com.michal.grud.movieCategorizationSystem.domain.movie.entity.jpa.MovieEntity;
import com.michal.grud.movieCategorizationSystem.domain.movie.repository.MovieRepository;
import com.michal.grud.movieCategorizationSystem.domain.movie.service.vlidation.MovieValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MovieValidatorTest {
    private static final long ONE_GIB = 1L * 1024 * 1024 * 1024;

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private MovieValidator validator;

    @Test
    void validateMovieCreateUpdate_shouldThrow_whenFileIsOver1GiB() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getSize()).thenReturn(ONE_GIB + 1);

        assertThatThrownBy(() -> validator.validateMovieCreateUpdate(file))
                .isInstanceOf(GlobalValidationException.class)
                .satisfies(ex -> {
                    GlobalValidationException e = (GlobalValidationException) ex;
                    assertThat(e.getErrorCode()).isEqualTo(MovieErrorType.TOO_LARGE_FILE.name());
                    assertThat(e.getDescription()).isEqualTo(MovieErrorType.TOO_LARGE_FILE.getDescription());
                });
    }

    @Test
    void validateMovieCreateUpdate_shouldNotThrow_whenFileIsExactly1GiB() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getSize()).thenReturn(ONE_GIB);
        assertThatCode(() -> validator.validateMovieCreateUpdate(file)).doesNotThrowAnyException();
    }

    @Test
    void validateMovieCreateUpdate_shouldNotThrow_whenFileIsNull() {
        assertThatCode(() -> validator.validateMovieCreateUpdate(null)).doesNotThrowAnyException();
    }

    @Test
    void validateTitleExistence_shouldThrow_whenTitleAlreadyExists() {
        String title = "The Matrix";
        MovieEntity existing = MovieEntity.builder().title(title).build();
        when(movieRepository.findById(title)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> validator.validateTitleExistence(title))
                .isInstanceOf(GlobalValidationException.class)
                .satisfies(ex -> {
                    GlobalValidationException e = (GlobalValidationException) ex;
                    assertThat(e.getErrorCode()).isEqualTo(MovieErrorType.MOVIE_ALREADY_EXISTS.name());
                    assertThat(e.getDescription()).isEqualTo(MovieErrorType.MOVIE_ALREADY_EXISTS.getDescription());
                });

        verify(movieRepository).findById(title);
        verifyNoMoreInteractions(movieRepository);
    }

    @Test
    void validateTitleExistence_shouldNotThrow_whenTitleNotExists() {
        String title = "Non-Existing";
        when(movieRepository.findById(title)).thenReturn(Optional.empty());

        assertThatCode(() -> validator.validateTitleExistence(title)).doesNotThrowAnyException();
        verify(movieRepository).findById(title);
        verifyNoMoreInteractions(movieRepository);
    }
}
