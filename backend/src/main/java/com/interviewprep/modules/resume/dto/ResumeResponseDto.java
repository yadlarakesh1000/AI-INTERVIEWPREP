package com.interviewprep.modules.resume.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeResponseDto {

    private Long id;
    private Long userId;
    private String fileName;
    private String extractedText;

    // Raw JSON string of the structured resume data; NULL until STEP 019 populates it.
    private String parsedData;
}
