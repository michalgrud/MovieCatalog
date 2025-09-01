package com.michal.grud.movieCategorizationSystem.domain.movie.client.digikat.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DigiKatResponse {

    private String title;
    private int productionType;
    private List<VODType> availableAtVODs;
    private UserScoreType usersScore;
    private String lastUsersScoreUpdate;


    public enum UserScoreType {mierny, dobry, wybitny}

    public enum VODType {netflix, youtube, disney, hbo}
}
