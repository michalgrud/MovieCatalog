package com.michal.grud.movieCategorizationSystem.common.exception;

import com.michal.grud.movieCategorizationSystem.common.response.GlobalErrorResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalHandlerException {
    @ExceptionHandler(GlobalValidationException.class)
    public ResponseEntity<GlobalErrorResponseDTO> handleCustomException(GlobalValidationException exception) {
        GlobalErrorResponseDTO responseDTO = GlobalErrorResponseDTO.builder()
                .description(exception.getDescription())
                .errorCode(exception.getErrorCode())
                .build();
        return ResponseEntity.badRequest().body(responseDTO);
    }
}
