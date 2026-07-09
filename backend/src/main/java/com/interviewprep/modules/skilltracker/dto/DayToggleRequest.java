package com.interviewprep.modules.skilltracker.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request body for PUT /api/skill-tracker/toggle (Section 8.6).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DayToggleRequest {

    @NotNull(message = "weekNumber is required")
    @Min(value = 1, message = "weekNumber must be at least 1")
    private Integer weekNumber;

    @NotNull(message = "dayNumber is required")
    @Min(value = 1, message = "dayNumber must be between 1 and 7")
    @Max(value = 7, message = "dayNumber must be between 1 and 7")
    private Integer dayNumber;
}
