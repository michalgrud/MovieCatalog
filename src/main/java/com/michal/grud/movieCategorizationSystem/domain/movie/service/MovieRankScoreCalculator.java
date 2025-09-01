package com.michal.grud.movieCategorizationSystem.domain.movie.service;

import com.michal.grud.movieCategorizationSystem.domain.movie.client.digikat.DigiKatClient;
import com.michal.grud.movieCategorizationSystem.domain.movie.client.digikat.dto.DigiKatResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class MovieRankScoreCalculator {

    private final DigiKatClient digiKatClient;

    public int calculateRankScore(String title, long size) {
        DigiKatResponse digiKatResponse = digiKatClient.getMovieDetails(title);

        int score = 0;
        if (size <= 200 * 1024 * 1024) return 100;
        if (digiKatResponse.getProductionType() == 0 || digiKatResponse.getProductionType() == 2) score += 200;
        if (digiKatResponse.getAvailableAtVODs().contains(DigiKatResponse.VODType.netflix)) score -= 50;
        if (digiKatResponse.getUsersScore().equals(DigiKatResponse.UserScoreType.wybitny)) score += 100;

        return score;
    }
}
