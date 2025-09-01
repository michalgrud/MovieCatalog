package com.michal.grud.movieCategorizationSystem.common.exception;

import lombok.Getter;

@Getter
public class GlobalValidationException extends RuntimeException {

    private final String errorCode;
    private final String description;

    public GlobalValidationException(String errorCode, String description) {
        super(description);
        this.errorCode = errorCode;
        this.description = description;
    }

    public GlobalValidationException(String errorCode, String description, Throwable cause) {
        super(description, cause);
        this.errorCode = errorCode;
        this.description = description;
    }

}