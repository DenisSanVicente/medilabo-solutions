package com.medilabo.front_service.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Note {

    private String id;
    private Long patId;
    private String patient;
    private String note;
}
