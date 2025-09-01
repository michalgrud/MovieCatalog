package com.michal.grud.movieCategorizationSystem.common.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GlobalErrorResponseDTO {
    private String errorCode;
    private String description;
}
