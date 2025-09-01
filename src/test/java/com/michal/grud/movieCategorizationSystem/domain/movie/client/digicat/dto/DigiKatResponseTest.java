package com.michal.grud.movieCategorizationSystem.domain.movie.client.digicat.dto;

import com.michal.grud.movieCategorizationSystem.domain.movie.client.digikat.dto.DigiKatResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class DigiKatResponseTest {


    @Test
    void builderShouldSetAllFields() {
        DigiKatResponse dto = DigiKatResponse.builder()
                .title("The Matrix")
                .productionType(1)
                .availableAtVODs(List.of(
                        DigiKatResponse.VODType.netflix,
                        DigiKatResponse.VODType.hbo
                ))
                .usersScore(DigiKatResponse.UserScoreType.dobry)
                .lastUsersScoreUpdate("2025-08-31")
                .build();

        assertThat(dto.getTitle()).isEqualTo("The Matrix");
        assertThat(dto.getProductionType()).isEqualTo(1);
        assertThat(dto.getAvailableAtVODs())
                .containsExactly(DigiKatResponse.VODType.netflix, DigiKatResponse.VODType.hbo);
        assertThat(dto.getUsersScore()).isEqualTo(DigiKatResponse.UserScoreType.dobry);
        assertThat(dto.getLastUsersScoreUpdate()).isEqualTo("2025-08-31");
    }

    @Test
    void settersShouldModifyValuesOnBuiltInstance() {
        DigiKatResponse dto = DigiKatResponse.builder()
                .title("Old")
                .productionType(0)
                .availableAtVODs(List.of(DigiKatResponse.VODType.youtube))
                .usersScore(DigiKatResponse.UserScoreType.mierny)
                .lastUsersScoreUpdate("2020-01-01")
                .build();

        dto.setTitle("New");
        dto.setProductionType(2);
        dto.setAvailableAtVODs(List.of(DigiKatResponse.VODType.disney));
        dto.setUsersScore(DigiKatResponse.UserScoreType.wybitny);
        dto.setLastUsersScoreUpdate("2025-08-31");

        assertThat(dto.getTitle()).isEqualTo("New");
        assertThat(dto.getProductionType()).isEqualTo(2);
        assertThat(dto.getAvailableAtVODs()).containsExactly(DigiKatResponse.VODType.disney);
        assertThat(dto.getUsersScore()).isEqualTo(DigiKatResponse.UserScoreType.wybitny);
        assertThat(dto.getLastUsersScoreUpdate()).isEqualTo("2025-08-31");
    }

    @Test
    void equalsAndHashCodeShouldDependOnAllFields() {
        DigiKatResponse a = DigiKatResponse.builder()
                .title("A")
                .productionType(1)
                .availableAtVODs(List.of(DigiKatResponse.VODType.netflix))
                .usersScore(DigiKatResponse.UserScoreType.dobry)
                .lastUsersScoreUpdate("2025-08-31")
                .build();

        DigiKatResponse b = DigiKatResponse.builder()
                .title("A")
                .productionType(1)
                .availableAtVODs(List.of(DigiKatResponse.VODType.netflix))
                .usersScore(DigiKatResponse.UserScoreType.dobry)
                .lastUsersScoreUpdate("2025-08-31")
                .build();

        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());

        b.setUsersScore(DigiKatResponse.UserScoreType.mierny);
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void toStringShouldContainKeyFields() {
        DigiKatResponse dto = DigiKatResponse.builder()
                .title("Kiler")
                .productionType(1)
                .availableAtVODs(List.of(DigiKatResponse.VODType.youtube))
                .usersScore(DigiKatResponse.UserScoreType.dobry)
                .lastUsersScoreUpdate("2025-08-31")
                .build();

        String s = dto.toString();
        assertThat(s).contains("DigiKatResponse");
        assertThat(s).contains("title=Kiler");
        assertThat(s).contains("productionType=1");
        assertThat(s).contains("youtube");
        assertThat(s).contains("dobry");
    }
}