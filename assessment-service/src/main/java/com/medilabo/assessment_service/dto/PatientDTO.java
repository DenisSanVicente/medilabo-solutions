package com.medilabo.assessment_service.dto;

import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PatientDTO {

    private Long id;
    private LocalDate birth;
    private String gender;
}
