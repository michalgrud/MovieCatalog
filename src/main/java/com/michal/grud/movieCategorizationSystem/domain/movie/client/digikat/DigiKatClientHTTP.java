package com.michal.grud.movieCategorizationSystem.domain.movie.client.digikat;

import com.michal.grud.movieCategorizationSystem.domain.movie.client.digikat.dto.DigiKatResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Component
@Profile("prod")
public class DigiKatClientHTTP implements DigiKatClient {
    private static final String DIGI_KAT_GET_MOVIE_DETAILS_URL = "/ranking?film=";
    private static final int TIMEOUT_DURATION = 60;
    @Value("${digikat.base-url}")
    private String DIGI_KAT_BASE_URL;

    @Override
    public DigiKatResponse getMovieDetails(String title) {
        return getWebClientBuilder()
                .build()
                .get()
                .uri(DIGI_KAT_BASE_URL + DIGI_KAT_GET_MOVIE_DETAILS_URL + title)
                .retrieve()
                .toEntity(DigiKatResponse.class)
                .timeout(Duration.ofSeconds(TIMEOUT_DURATION))
                .block()
                .getBody();
    }

    private WebClient.Builder getWebClientBuilder() {
        HttpClient httpClient = HttpClient
                .create()
                .wiretap(true);

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient));
    }
}
