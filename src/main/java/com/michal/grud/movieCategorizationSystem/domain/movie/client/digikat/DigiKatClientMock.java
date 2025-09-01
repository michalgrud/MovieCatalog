package com.michal.grud.movieCategorizationSystem.domain.movie.client.digikat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.michal.grud.movieCategorizationSystem.common.exception.GlobalValidationException;
import com.michal.grud.movieCategorizationSystem.domain.movie.client.digikat.dto.DigiKatResponse;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@Profile({"local", "test"})
@RequiredArgsConstructor
public class DigiKatClientMock implements DigiKatClient {

    private final ObjectMapper objectMapper;
    @Value("classpath*:/mocks/**/*.json")
    private Resource[] mockFiles;
    private List<DigiKatResponse> cache = List.of();

    @Override
    public DigiKatResponse getMovieDetails(String title) {
        return cache.stream()
                .filter(item -> item.getTitle().equals(title))
                .findFirst()
                .orElseThrow(() -> new GlobalValidationException(DigiKatErrorType.DIGIKAT_MOVIE_NOT_FOUND.name(), DigiKatErrorType.DIGIKAT_MOVIE_NOT_FOUND.getDescription()));//TODO
    }


    @PostConstruct
    private void loadAll() {
        List<DigiKatResponse> result = new ArrayList<>();

        for (Resource res : mockFiles) {
            if (!res.isReadable()) continue;

            try (InputStream in = res.getInputStream()) {
                try {
                    DigiKatResponse one = objectMapper.readValue(in, DigiKatResponse.class);
                    result.add(one);
                } catch (MismatchedInputException e) {
                    try (InputStream in2 = res.getInputStream()) {
                        List<DigiKatResponse> many = objectMapper.readValue(
                                in2, new TypeReference<List<DigiKatResponse>>() {
                                });
                        result.addAll(many);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Error occurred while loading mocks: " + res.getDescription(), e);
            }
        }

        Map<String, DigiKatResponse> byTitle = new LinkedHashMap<>();
        for (DigiKatResponse d : result) {
            if (d.getTitle() != null) byTitle.put(d.getTitle(), d);
        }
        this.cache = List.copyOf(byTitle.values());
    }
}
