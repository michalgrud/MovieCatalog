package com.michal.grud.movieCategorizationSystem.domain.movie.client.digikat;

import lombok.Getter;

@Getter
public enum DigiKatErrorType {
    DIGIKAT_MOVIE_NOT_FOUND("Movie not found in DigiKat");
    private String description;

    private DigiKatErrorType(String description) {
        this.description = description;
    }
}
