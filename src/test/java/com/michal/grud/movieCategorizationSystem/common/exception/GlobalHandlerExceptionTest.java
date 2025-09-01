package com.michal.grud.movieCategorizationSystem.common.exception;

import com.michal.grud.movieCategorizationSystem.common.response.GlobalErrorResponseDTO;
import com.michal.grud.movieCategorizationSystem.domain.movie.entity.error.MovieErrorType;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class GlobalHandlerExceptionTest {

    @Test
    void shouldMapGlobalValidationExceptionToBadRequest() {
        GlobalHandlerException handler = new GlobalHandlerException();
        GlobalValidationException ex = new GlobalValidationException(MovieErrorType.TOO_LARGE_FILE.name(), MovieErrorType.TOO_LARGE_FILE.getDescription());

        ResponseEntity<GlobalErrorResponseDTO> resp = handler.handleCustomException(ex);

        assertThat(resp.getStatusCode().value()).isEqualTo(400);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().getErrorCode()).isEqualTo(MovieErrorType.TOO_LARGE_FILE.name());
        assertThat(resp.getBody().getDescription()).isEqualTo(MovieErrorType.TOO_LARGE_FILE.getDescription());
    }
}
