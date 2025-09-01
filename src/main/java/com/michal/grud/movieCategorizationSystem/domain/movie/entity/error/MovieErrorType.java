package com.michal.grud.movieCategorizationSystem.domain.movie.entity.error;

import lombok.Getter;

@Getter
public enum MovieErrorType {
    MOVIE_NOT_FOUND("Movie not found"),
    MOVIE_ALREADY_EXISTS("Movie already exists"),
    TOO_LARGE_FILE("File is too large, Should be less than 1GB");
    private String description;

    private MovieErrorType(String description) {
        this.description = description;
    }
}
