package com.michal.grud.movieCategorizationSystem.domain.movie.client.digikat;

import com.michal.grud.movieCategorizationSystem.domain.movie.client.digikat.dto.DigiKatResponse;

public interface DigiKatClient {

    DigiKatResponse getMovieDetails(String title);
}
