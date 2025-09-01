package com.michal.grud.movieCategorizationSystem.domain.movie.service;

import com.michal.grud.movieCategorizationSystem.domain.movie.client.digikat.DigiKatClient;
import com.michal.grud.movieCategorizationSystem.domain.movie.client.digikat.dto.DigiKatResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MovieRankScoreCalculatorTest {
    private static final long LIMIT_200_MB = 200L * 1024 * 1024;

    @Mock
    private DigiKatClient digiKatClient;

    @InjectMocks
    private MovieRankScoreCalculator calculator;

    private static DigiKatResponse resp(int productionType,
                                        List<DigiKatResponse.VODType> vods,
                                        DigiKatResponse.UserScoreType score) {
        return DigiKatResponse.builder()
                .title("any")
                .productionType(productionType)
                .availableAtVODs(vods)
                .usersScore(score)
                .lastUsersScoreUpdate("2025-08-31")
                .build();
    }

    @Test
    void shouldReturn100_whenSizeIsExactly200MB() {
        String title = "T1";
        when(digiKatClient.getMovieDetails(title))
                .thenReturn(resp(1, List.of(), DigiKatResponse.UserScoreType.mierny));

        int result = calculator.calculateRankScore(title, LIMIT_200_MB);

        assertThat(result).isEqualTo(100);
        verify(digiKatClient).getMovieDetails(title);
    }

    @Test
    void shouldReturn100_whenSizeIsBelow200MB() {
        String title = "T2";
        when(digiKatClient.getMovieDetails(title))
                .thenReturn(resp(1, List.of(), DigiKatResponse.UserScoreType.mierny));

        int result = calculator.calculateRankScore(title, LIMIT_200_MB - 1);

        assertThat(result).isEqualTo(100);
        verify(digiKatClient).getMovieDetails(title);
    }

    @Test
    void shouldReturn0_whenNoConditionsMatch_andSizeAbove200MB() {
        String title = "T3";

        when(digiKatClient.getMovieDetails(title))
                .thenReturn(resp(1, List.of(), DigiKatResponse.UserScoreType.mierny));

        int result = calculator.calculateRankScore(title, LIMIT_200_MB + 1);

        assertThat(result).isZero();
        verify(digiKatClient).getMovieDetails(title);
    }

    @Test
    void shouldAdd200_whenProductionTypeIs0() {
        String title = "T4";
        when(digiKatClient.getMovieDetails(title))
                .thenReturn(resp(0, List.of(), DigiKatResponse.UserScoreType.mierny));

        int result = calculator.calculateRankScore(title, LIMIT_200_MB + 1);

        assertThat(result).isEqualTo(200);
        verify(digiKatClient).getMovieDetails(title);
    }

    @Test
    void shouldAdd200_whenProductionTypeIs2() {
        String title = "T5";
        when(digiKatClient.getMovieDetails(title))
                .thenReturn(resp(2, List.of(), DigiKatResponse.UserScoreType.mierny));

        int result = calculator.calculateRankScore(title, LIMIT_200_MB + 1);

        assertThat(result).isEqualTo(200);
        verify(digiKatClient).getMovieDetails(title);
    }

    @Test
    void shouldSubtract50_whenAvailableOnNetflix() {
        String title = "T6";
        when(digiKatClient.getMovieDetails(title))
                .thenReturn(resp(1, List.of(DigiKatResponse.VODType.netflix), DigiKatResponse.UserScoreType.mierny));

        int result = calculator.calculateRankScore(title, LIMIT_200_MB + 1);

        assertThat(result).isEqualTo(-50);
        verify(digiKatClient).getMovieDetails(title);
    }

    @Test
    void shouldAdd100_whenUsersScoreIsWybitny() {
        String title = "T7";
        when(digiKatClient.getMovieDetails(title))
                .thenReturn(resp(1, List.of(), DigiKatResponse.UserScoreType.wybitny));

        int result = calculator.calculateRankScore(title, LIMIT_200_MB + 1);

        assertThat(result).isEqualTo(100);
        verify(digiKatClient).getMovieDetails(title);
    }

    @Test
    void shouldCombineAllRulesCorrectly() {
        String title = "T8";
        when(digiKatClient.getMovieDetails(title))
                .thenReturn(resp(0,
                        List.of(DigiKatResponse.VODType.netflix),
                        DigiKatResponse.UserScoreType.wybitny));

        int result = calculator.calculateRankScore(title, LIMIT_200_MB + 1);

        assertThat(result).isEqualTo(250);
        verify(digiKatClient).getMovieDetails(title);
    }
}
